(ns lib-scraper.scraper.traverser
  (:require [hickory.zip :as hzip]
            [clojure.zip :as zip]
            [datascript.core :as d]
            [lib-scraper.helpers.zip :refer [loc-at-node? is-parent?]]
            [lib-scraper.model.core :as m]))

(defn- queue
  ([] (clojure.lang.PersistentQueue/EMPTY))
  ([& s]
   (reduce conj clojure.lang.PersistentQueue/EMPTY s)))

(defn- tempid
  []
  (d/tempid nil))

(def step-types {:following (constantly [identity zip/next zip/end?])
                 :children (constantly [zip/down zip/right some?])
                 :siblings (constantly [zip/leftmost zip/right some?])
                 :follwing-siblings (constantly [zip/right zip/right some?])
                 :preceding-siblings (constantly [zip/left zip/left some?])
                 :descendants (fn [loc]
                                [identity zip/next
                                 #(and (not (zip/end? %))
                                       (is-parent? loc %))])})

(defn select-locs-spread-step
  [type loc]
  (let [[init next continue?] ((step-types type) loc)]
    (take-while continue? (iterate next (init loc)))))

(defn select-locs
  [selectors loc]
  (loop [[selector & selectors] selectors
         locs [loc]]
    (if-not selector locs
      (recur selectors
             (if (keyword? selector)
               (mapcat (partial select-locs-spread-step selector) locs)
               (keep selector locs))))))

(defmulti trigger-hook*
  (fn [hook stack loc]
    (some (set (keys hook)) [:concept :attribute])))

(defmethod trigger-hook* :concept
  [{:keys [concept ref-from-trigger ref-to-trigger]} stack loc]
  (let [id (tempid)
        parent (some :id stack)
        tx (cond-> [[:db/add id :type concept]]
             ref-from-trigger (conj [:db/add parent ref-from-trigger id])
             ref-to-trigger (conj [:db/add id ref-to-trigger parent]))]
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
     :entry (when type {:id id, :type type, :loc loc})}))

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
