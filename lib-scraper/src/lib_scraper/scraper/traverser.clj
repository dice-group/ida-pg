(ns lib-scraper.scraper.traverser
  (:require [datascript.core :as d]
            [hickory.zip :as hzip]
            [clojure.tools.logging :as log]
            [lib-scraper.helpers.zip :as lzip]
            [lib-scraper.scraper.postprocessor :as pp])
  (:import (java.util.regex Pattern)))

(defn- queue
  ([] (clojure.lang.PersistentQueue/EMPTY))
  ([& s] (reduce conj clojure.lang.PersistentQueue/EMPTY s)))

(defn- tempid [] (d/tempid nil))

(defn- resolve-value
  [value transform stack index loc]
  (let [value (case (or value :content)
                :content (clojure.string/trim (lzip/loc-content loc))
                :trigger-index (:index (first stack)))]
    (cond
      (instance? Pattern transform) (re-find transform value)
      (fn? transform) (transform value)
      :else value)))

(defmulti trigger-hook*
  (fn [hook stack index loc]
    (some (set (keys hook)) [:concept :attribute])))

(defmethod trigger-hook* :concept
  [{:keys [concept ref-from-trigger ref-to-trigger]} stack index loc]
  (let [id (tempid)
        parent (some :id stack)
        tx (cond-> [[:db/add id :type concept]
                    [:db/add id :tempid id]]
             ref-from-trigger (conj [:db/add parent ref-from-trigger id])
             ref-to-trigger (conj [:db/add id ref-to-trigger parent]))]
    {:tx tx
     :type concept
     :id id}))

(defmethod trigger-hook* :attribute
  [{:keys [attribute value transform]} stack index loc]
  (when-let [id (some :id stack)]
    (if-let [value (resolve-value value transform stack index loc)]
      {:tx [[:db/add id attribute value]]
       :type attribute})))

(defmethod trigger-hook* :default
  [hook stack index loc]
  (log/error (str "Unknown hook type: " hook)))

(defn trigger-hook
  [hook stack index loc]
  (if-let [{:keys [tx type id]} (trigger-hook* hook stack index loc)]
    {:tx tx
     :entry (when type {:id id, :type type, :loc loc, :index index})}))

(defn trigger-hooks
  [hooks stack]
  (let [[{:keys [type loc]}] stack
        triggered (hooks type)]
    (mapcat (fn [{:keys [selector limit] :as hook}]
              (let [selection (lzip/select-locs selector loc)]
                (keep-indexed (partial trigger-hook hook stack)
                              (if limit (take limit selection) selection))))
            triggered)))

(defn traverse!
  [conn hooks ecosystem doc url]
  (let [tx (transient [[:db/add :db/current-tx :source url]])
        [tx ids] (loop [merged-tx tx
                        merged-ids (transient #{})
                        queue (queue (list {:type :document
                                            :loc (hzip/hickory-zip doc)}))]
                   (if (empty? queue)
                     [merged-tx (persistent! merged-ids)]
                     (let [stack (peek queue)
                           effects (trigger-hooks hooks stack)
                           tx (mapcat :tx effects)
                           ids (keep (comp :id :entry) effects)
                           stacks (->> effects
                                       (keep :entry)
                                       (map #(cons % stack)))]
                       (recur (reduce conj! merged-tx tx)
                              (reduce conj! merged-ids ids)
                              (-> queue (pop) (into stacks))))))
        tx (conj! tx [:db.fn/call pp/postprocess-transactions ecosystem ids])]
    (d/transact! conn (persistent! tx))))

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

(def db-spec {:type {:db/type :db.type/keyword
                     :db/doc "Type of the entity. Used to lookup specs for concepts."}
              :source {:db/type :db.type/string
                       :db/doc "The datasource this entity originates from. Typically a URL."}
              :tempid {:db/type :db.type/long
                       :db/cardinality :db.cardinality/many
                       :db/unique :db.unique/identity
                       :db/doc "Temporary bookkeeping property used by the scraper."}})

(defn traverser
  [{:keys [hooks patterns ecosystem]}]
  (let [conn (d/create-conn (merge db-spec (:attributes ecosystem)))
        hooks (index-hooks hooks patterns)]
    {:conn conn
     :traverser (partial traverse! conn hooks ecosystem)}))
