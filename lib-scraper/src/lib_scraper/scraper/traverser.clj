(ns lib-scraper.scraper.traverser
  (:require [datascript.core :as d]
            [flatland.ordered.set :refer [ordered-set]]
            [hickory.zip :as hzip]
            [clojure.tools.logging :as log]
            [lib-scraper.helpers.zip :as lzip]
            [lib-scraper.helpers.map :as map]
            [lib-scraper.scraper.postprocessor :as pp])
  (:import (java.util.regex Pattern)))

(defn- queue
  ([] (clojure.lang.PersistentQueue/EMPTY))
  ([& s] (reduce conj clojure.lang.PersistentQueue/EMPTY s)))

(defn- tempid [] (d/tempid nil))

(defn- unique-attr?
  [attrs attr]
  (-> (attrs attr) :db/unique (= :db.unique/identity)))

(defn- indexing-tx?
  [attrs [_ _ attr _]]
  (unique-attr? attrs attr))

(defn- resolve-value
  [value transform parent index loc]
  (let [value (case (or value :content)
                :content (clojure.string/trim (lzip/loc-content loc))
                :trigger-index (:index parent))]
    (if transform (transform value) value)))

(defmulti trigger-hook*
  (fn [hook stack ecosystem index loc]
    (some (set (keys hook)) [:concept :attribute])))

(defmethod trigger-hook* :concept
  [{:keys [concept ref-from-trigger ref-to-trigger]} [parent] _ _ _]
  (let [id (tempid)
        pid (:id parent)
        tx (cond-> [[:db/add id :tempid id]
                    [:db/add id :type concept]]
             ref-from-trigger (conj [:db/add pid ref-from-trigger id])
             ref-to-trigger (conj [:db/add id ref-to-trigger pid]))]
    {:id id
     :type concept
     :tx tx}))

(defmethod trigger-hook* :attribute
  [{:keys [attribute value transform]} [parent] {:keys [preprocessors attributes]} index loc]
  (if-let [value (resolve-value value transform parent index loc)]
    (let [{:keys [id type]} parent
          processor (get-in preprocessors [type attribute])
          tx (conj (if processor (processor value id) [])
                   [:db/add id attribute value])
          {itx true, tx false} (group-by (partial indexing-tx? attributes) tx)]
      {:id id
       :type attribute
       :itx itx
       :tx tx})))

(defmethod trigger-hook* :default
  [hook _ _ _ _]
  (log/error (str "Unknown hook type: " hook)))

(defn trigger-hook
  [hook stack ecosystem index loc]
  (if-let [{:keys [itx tx type id]} (trigger-hook* hook stack ecosystem index loc)]
    {:itx itx
     :tx tx
     :entry (when type {:id id, :type type, :loc loc, :index index})}))

(defn trigger-hooks
  [extends hooks stack ecosystem]
  (let [[{:keys [type loc]}] stack
        triggered (mapcat hooks (extends type))]
    (mapcat (fn [{:keys [selector limit] :as hook}]
              (let [selection (lzip/select-locs selector loc)]
                (keep-indexed (partial trigger-hook hook stack ecosystem)
                              (if limit (take limit selection) selection))))
            triggered)))

(defn traverse!
  [conn hooks ecosystem doc url]
  (let [; index transactions:
        itx (transient [])
        ; non-index transactions:
        tx (transient [[:db/add :db/current-tx :source url]])
        extends (assoc (:extends ecosystem) :document #{:document})
        [itx tx ids] (loop [merged-itx itx
                            merged-tx tx
                            merged-ids (transient (ordered-set))
                            queue (queue (list {:type :document
                                                :loc (hzip/hickory-zip doc)}))]
                       (if (empty? queue)
                         [merged-itx merged-tx merged-ids]
                         (let [stack (peek queue)
                               effects (trigger-hooks extends hooks stack ecosystem)
                               itx (mapcat :itx effects)
                               tx (mapcat :tx effects)
                               ids (keep (comp :id :entry) effects)
                               stacks (->> effects
                                           (keep :entry)
                                           (map #(cons % stack)))]
                           (recur (reduce conj! merged-itx itx)
                                  (reduce conj! merged-tx tx)
                                  (reduce conj! merged-ids ids)
                                  (-> queue (pop) (into stacks))))))
        itx (persistent! itx)
        ids (persistent! ids)
        tx (-> tx
               (conj! [:db.fn/call pp/postprocess-transactions ecosystem ids])
               (persistent!))]
    (d/transact! conn (concat itx tx))))

(defn index-hooks
  [hooks patterns]
  (->> hooks
       (map (fn [hook]
              (if-let [pattern (patterns (:pattern hook))]
                (merge pattern hook)
                hook)))
       (mapcat (fn [hook]
                 (if (seqable? (:trigger hook))
                   (map (partial assoc hook :trigger) (:trigger hook))
                   [hook])))
       (group-by :trigger)))

(defn resolve-hook-aliases
  [hooks {:keys [concept-aliases attribute-aliases]}]
  (map/map-kv (fn [[k v]]
                [(concept-aliases k k)
                 (mapv #(-> %
                            (map/update-keys [:concept] concept-aliases)
                            (map/update-keys [:attribute
                                              :ref-from-trigger
                                              :ref-to-trigger]
                                             attribute-aliases))
                       v)])
              hooks))

(def db-spec {:source {:db/doc "The datasource this entity originates from. Typically a URL."}
              :tempid {:db/cardinality :db.cardinality/many
                       :db/unique :db.unique/identity
                       :db/doc "Temporary bookkeeping property used by the scraper."}})

(defn traverser
  [{:keys [hooks patterns ecosystem]}]
  (let [conn (d/create-conn (merge db-spec (:attributes ecosystem)))
        hooks (-> hooks
                  (index-hooks patterns)
                  (resolve-hook-aliases ecosystem))]
    {:conn conn
     :traverser (partial traverse! conn hooks ecosystem)}))
