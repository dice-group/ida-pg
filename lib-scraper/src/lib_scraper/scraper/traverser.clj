(ns lib-scraper.scraper.traverser
  (:require [hickory.zip :as hzip]
            [clojure.zip :as zip]
            [datascript.core :as d]
            [lib-scraper.helpers.zip :as lzip]
            [lib-scraper.model.core :as m]))

(defn- queue
  ([] (clojure.lang.PersistentQueue/EMPTY))
  ([& s]
   (reduce conj clojure.lang.PersistentQueue/EMPTY s)))

(defn- tempid
  []
  (d/tempid nil))

(defmulti trigger-hook*
  (fn [hook stack index loc]
    (some (set (keys hook)) [:concept :attribute])))

(defmethod trigger-hook* :concept
  [{:keys [concept ref-from-trigger ref-to-trigger]} stack index loc]
  (let [id (tempid)
        parent (some :id stack)
        tx (cond-> [[:db/add id :type concept]]
             ref-from-trigger (conj [:db/add parent ref-from-trigger id])
             ref-to-trigger (conj [:db/add id ref-to-trigger parent]))]
    {:tx tx
     :type concept
     :id id}))

(defmethod trigger-hook* :attribute
  [{:keys [attribute value] :or {value :content}} stack index loc]
  (when-let [id (some :id stack)]
    (let [value (case value
                  :content (lzip/loc-content loc)
                  :trigger-index (:index (first stack)))]
      {:tx [[:db/add id attribute value]]
       :type attribute})))

(defmethod trigger-hook* :ref
  [{:keys [ref from to]}])

(defmethod trigger-hook* :default
  [hook stack index loc]
  (println (str "Unknown hook type: " hook)))

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
  [conn hooks doc]
  (let [merged-tx (transient [])]
    (loop [queue (queue (list {:type :document
                               :loc (hzip/hickory-zip doc)}))]
      (when (seq queue)
        (let [stack (peek queue)
              effects (trigger-hooks hooks stack)
              tx (mapcat :tx effects)
              stacks (->> effects
                          (keep :entry)
                          (map #(cons % stack)))]
          (run! (partial conj! merged-tx) tx)
          (recur (-> queue (pop) (into stacks))))))
    (d/transact! conn (persistent! merged-tx))))

(defn index-hooks
  [hooks patterns]
  (->> hooks
       (map (fn [hook]
              (if-let [pattern (patterns (:pattern hook))]
                (merge pattern hook)
                hook)))
       (group-by :trigger)))

(defn traverser
  [hooks patterns]
  (let [conn (m/conn)
        hooks (index-hooks hooks patterns)]
    {:conn conn
     :traverser (partial traverse! conn hooks)}))
