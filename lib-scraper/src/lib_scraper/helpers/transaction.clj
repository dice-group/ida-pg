(ns lib-scraper.helpers.transaction
  (:require [datascript.core :as d])
  (:refer-clojure :exclude [merge]))

(defn add-attr
  ([attr]
   (add-attr attr identity))
  ([attr f]
   (fn [db id]
     (if-let [v (f (if (d/db? db) (d/entity db id) db))]
       [[:db/add id attr v]]
       []))))

(defn merge
  [& fns]
  (let [fns (disj (set fns) nil)]
    (case (count fns)
      0 (constantly [])
      1 (first fns)
      (let [calls (mapv #(vector :db.fn/call %) fns)]
        (fn [db & args]
          (mapv #(into % args) calls))))))

(defn merge-direct
  [& fns]
  (let [fns (disj (set fns) nil)]
    (case (count fns)
      0 (constantly [])
      1 (first fns)
      (fn [& args]
        (mapcat #(apply % args) fns)))))

(defn tempid?
  ^Boolean [x]
  (or (and (number? x) (neg? x)) (string? x)))

(defn unique-attr?
  [attr]
  (-> attr :db/unique (= :db.unique/identity)))

(defn ref-attr?
  [attr]
  (-> attr :db/valueType (= :db.type/ref)))

(defn indexing-tx?
  [attrs [_ _ attr _]]
  (unique-attr? (attrs attr)))

(defn ref-tx?
  [attrs [_ _ attr _]]
  (ref-attr? (attrs attr)))

(defn replace-ids
  [m f]
  (fn [& args]
    (map (fn [tx]
           (cond
             (sequential? tx)
             (let [[op e a v] tx]
               (case op
                 :db/add [op (get m e e) a v]
                 :db.fn/call (into [op (replace-ids m e)]
                                   (nnext tx))
                 tx))
             (and (map? tx) (contains? m (:db/id tx)))
             (update tx :db/id m)
             :else tx))
         (apply f args))))
