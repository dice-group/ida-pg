(ns librarian.helpers.transaction
  (:require [datascript.core :as d]
            [datascript.db :as db])
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

(defn many-attr?
  [attr]
  (-> attr :db/cardinality (= :db.cardinality/many)))

(defn indexing-tx?
  [attrs [_ _ attr _]]
  (unique-attr? (attrs attr)))

(defn ref-tx?
  [attrs [_ _ attr _]]
  (ref-attr? (attrs attr)))

(declare replace-ids)

(defn replace-ids-in-tx
  [attrs m tx]
  (cond
    (sequential? tx)
    (let [[op e a v] tx]
      (case op
        :db/add [op (get m e e) a
                 (if (ref-attr? (attrs a)) (get m v v) v)]
        :db.fn/call (into [op (replace-ids attrs m e)]
                          (nnext tx))
        tx))
    (and (map? tx) (contains? m (:db/id tx)))
    (update tx :db/id m)
    :else tx))

(defn replace-ids
  [attrs m f]
  (fn [& args]
    (map (partial replace-ids-in-tx attrs m)
         (apply f args))))

(defn clone-entities
  ([db ids]
   (clone-entities db ids []))
  ([db ids reverse-lookup-attrs]
   (let [schema (db/-schema db)
         new-ids (into {} (map (fn [id] [id (d/tempid nil)])) ids)]
     (into []
           (comp (mapcat #(concat (d/datoms db :eavt %)
                                  (mapcat (fn [a] (d/datoms db :avet a %))
                                          reverse-lookup-attrs)))
                 (distinct)
                 (map (fn [[e a v]]
                        [:db/add (new-ids e e) a
                         (if (ref-attr? (schema a))
                           (new-ids v v) v)])))
           ids))))
