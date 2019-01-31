(ns lib-scraper.scraper.traverser
  (:require [datascript.core :as d]
            [flatland.ordered.set :refer [ordered-set]]
            [hickory.zip :as hzip]
            [clojure.tools.logging :as log]
            [clojure.string :as string]
            [lib-scraper.helpers.zip :as lzip]
            [lib-scraper.helpers.map :as map]
            [lib-scraper.helpers.transaction :as tx]
            [lib-scraper.scraper.postprocessor :as pp])
  (:import (java.util.regex Pattern)))

(defn- queue
  ([] (clojure.lang.PersistentQueue/EMPTY))
  ([& s] (reduce conj clojure.lang.PersistentQueue/EMPTY s)))

(defn- tempid [] (d/tempid nil))

(defn- resolve-value
  [value transform parent index loc]
  (let [value (case (or value :content)
                :content (string/trim (lzip/loc-content loc))
                :trigger-index (:index parent))]
    (if transform (transform value) value)))

(defmulti trigger-hook*
  (fn [hook stack ecosystem index loc]
    (some (set (keys hook)) [:concept :attribute])))

(defmethod trigger-hook* :concept
  [{:keys [concept ref-from-trigger ref-to-trigger allow-incomplete]} [parent]
   {:keys [preprocessors attributes]} _ _]
  (let [id (tempid)
        [pid ptype] ((juxt :id :type) parent)
        tx-base (cond-> [[:db/add id :tempid id]
                         [:db/add id :type concept]]
                  allow-incomplete (conj [:db/add id :allow-incomplete true]))
        tx-from (when ref-from-trigger
                  (let [processor (get-in preprocessors [ptype ref-from-trigger])]
                    (conj (if processor (processor id pid) [])
                          [:db/add pid ref-from-trigger id])))
        tx-to (when ref-to-trigger
                (let [processor (get-in preprocessors [type ref-to-trigger])]
                  (conj (if processor (processor pid id) [])
                        [:db/add id ref-to-trigger pid])))
        {itx true, tx false} (group-by (partial tx/indexing-tx? attributes)
                                       (concat tx-from tx-to))]
    {:id id
     :type concept
     :triggers concept
     :itx itx :tx (concat tx-base tx)}))

(defmethod trigger-hook* :attribute
  [{:keys [attribute value transform]} [parent]
   {:keys [preprocessors attributes]} index loc]
  (when-let [value (resolve-value value transform parent index loc)]
    (let [{:keys [id type]} parent
          preprocessors (preprocessors type)
          tx (mapcat (fn [attribute]
                       (let [processor (get preprocessors attribute)]
                         (conj (if processor (processor value id) [])
                               [:db/add id attribute value])))
                     (if (seqable? attribute) attribute [attribute]))
          {itx true, tx false} (group-by (partial tx/indexing-tx? attributes) tx)]
      {:id id
       :type type
       :triggers attribute
       :itx itx :tx tx})))

(defmethod trigger-hook* :default
  [hook _ _ _ _]
  (log/error (str "Unknown hook type: " hook)))

(defn trigger-hook
  [hook stack ecosystem index loc]
  (when-let [{:keys [itx tx type triggers id]}
             (trigger-hook* hook stack ecosystem index loc)]
    {:itx itx :tx tx
     :entry (when triggers {:id id
                            :type type :triggers triggers
                            :loc loc :index index})}))

(defn trigger-hooks
  [extends hooks stack ecosystem]
  (let [[{:keys [triggers loc]}] stack
        triggered (mapcat hooks (extends triggers))]
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
        tx (transient [[:db/add :db/current-tx :tx/source url]])
        extends (assoc (:extends ecosystem) :document #{:document})
        [itx tx ids] (loop [merged-itx itx
                            merged-tx tx
                            merged-ids (transient (ordered-set))
                            queue (queue (list {:triggers :document
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

(defn finalize
  "Remove entities that were not completed by the end of the scrape."
  [conn]
  (let [db @conn
        res (d/q '[:find ?id :where [?id :allow-incomplete true]])]
    (d/db-with db (mapv (comp #(vector :db.fn/retractEntity %) first) res))))

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
                       :db/doc "Temporary bookkeeping property used by the scraper."}
              :allow-incomplete {:db/index true
                                 :db/doc "Temporary bookkeeping property used by the scraper."}})

(defn traverser
  [{:keys [hooks patterns ecosystem]}]
  (let [conn (d/create-conn (merge db-spec (:attributes ecosystem)))
        hooks (-> hooks
                  (index-hooks patterns)
                  (resolve-hook-aliases ecosystem))]
    {:traverser (partial traverse! conn hooks ecosystem)
     :finalize (partial finalize conn)}))
