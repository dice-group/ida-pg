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
  [{:keys [concept]} stack loc]
  (let [id (tempid)]
    {:tx [[:db/add id :type concept]]
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
    (println (d/transact! conn (persistent! merged-tx)))))

(defn index-hooks
  [hooks]
  (group-by :trigger hooks))

(defn traverser
  [hooks]
  (let [conn (m/conn)
        hooks (index-hooks hooks)]
    {:conn conn
     :traverser (partial traverse! conn hooks)}))
