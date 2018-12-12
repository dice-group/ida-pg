(ns lib-scraper.scraper.traverser
  (:require [hickory.select :as s]
            [hickory.zip :as hzip]
            [clojure.zip :as zip]
            [datascript.core :as d]
            [lib-scraper.model.core :as m]))

(defn- queue
  ([] (clojure.lang.PersistentQueue/EMPTY))
  ([& s]
   (reduce conj clojure.lang.PersistentQueue/EMPTY s)))

(defn- tempid
  []
  (d/tempid nil))

(defn select-locs
  [selector loc]
  (when-let [next (s/select-next-loc selector loc)]
    (lazy-seq (cons next (select-locs selector (zip/next next))))))

(defmulti trigger-hook*
  (fn [hook stack loc]
    (some (set (keys hook)) [:concept :attribute])))

(defmethod trigger-hook* :concept
  [{:keys [concept ref-from-trigger]} stack loc]
  (let [id (tempid)
        tx [[:db/add id :type concept]]
        tx (if ref-from-trigger
             (let [parent (some :id stack)]
               (conj tx [:db/add parent ref-from-trigger id]))
             tx)]
    {:tx tx
     :type concept
     :id id}))

(defmethod trigger-hook* :attribute
  [{:keys [attribute]} stack loc]
  (when-let [id (some :id stack)]
    (let [value (->> (zip/node loc)
                     :content
                     (filter string?)
                     (reduce str)
                     (clojure.string/trim))]
      {:tx [[:db/add id attribute value]]
       :type attribute})))

(defmethod trigger-hook* :default
  [hook stack loc]
  (println (str "Unknown hook type: " hook)))

(defn trigger-hook
  [hook stack loc]
  (if-let [{:keys [tx type id]} (trigger-hook* hook stack loc)]
    {:tx tx
     :entry (when type
              {:id id, :type type, :loc loc})}))

(defn trigger-hooks
  [hooks stack]
  (let [[{:keys [type loc]}] stack
        triggered (hooks type)]
    (mapcat (fn [{:keys [selector limit] :as hook}]
              (let [selection (select-locs selector loc)]
                (keep (partial trigger-hook hook stack)
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
