(ns librarian.helpers.transaction
  "A collection of helpers to work with datascript transactions."
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
  "Takes multiple datascript transaction functions (to be called via `:db.fn/call`) and returns a single datascript transaction function that will apply all the given ones in sequence.
   The functions will be applied lazily via a series of `:db.fn/call` transactions and can influence their successors."
  [& fns]
  (let [fns (disj (set fns) nil)]
    (case (count fns)
      0 (constantly [])
      1 (first fns)
      (let [calls (mapv #(vector :db.fn/call %) fns)]
        (fn [db & args]
          (mapv #(into % args) calls))))))

(defn merge-direct
  "Like `librarian.helpers.transaction/merge` but the given functions will be applied eagerly and without them being able to influence their successors, i.e. they all get the same database snapshot."
  [& fns]
  (let [fns (disj (set fns) nil)]
    (case (count fns)
      0 (constantly [])
      1 (first fns)
      (fn [& args]
        (mapcat #(apply % args) fns)))))

(defn tempid?
  "Returns whether `x` is a valid datascript tempid."
  ^Boolean [x]
  (or (and (number? x) (neg? x)) (string? x)))

(defn unique-attr?
  "Returns whether the given datascript attribute schema description describes a unique attribute."
  [attr]
  (-> attr :db/unique (= :db.unique/identity)))

(defn ref-attr?
  "Returns whether the given datascript attribute schema description describes a reference attribute."
  [attr]
  (-> attr :db/valueType (= :db.type/ref)))

(defn many-attr?
  "Returns whether the given datascript attribute schema description describes an attribute with cardinality `many`."
  [attr]
  (-> attr :db/cardinality (= :db.cardinality/many)))

(defn indexing-tx?
  "Returns whether the given transaction vector affects an indexed attribute.
   A datascript attribute schema `attrs` has to be provided."
  [attrs [_ _ attr _]]
  (unique-attr? (attrs attr)))

(defn ref-tx?
  "Returns whether the given transaction vector affects a reference attribute.
   A datascript attribute schema `attrs` has to be provided."
  [attrs [_ _ attr _]]
  (ref-attr? (attrs attr)))

(declare replace-ids)

(defn replace-ids-in-tx
  "Replaces the ids in the transaction vector or map `tx` via the id replacement map `m`.
   A datascript attribute schema `attrs` has to be provided.
   The id replacement is applied recursively to referenced ids and ids emitted by transaction functions."
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
  "Transforms a given datascript transaction function `f` into one which uses the id replacements provided in the replacement map `m`.
   Similar to `replace-ids-in-tx` but for function transformation instead of transaction transformation.
   A datascript attribute schema `attrs` has to be provided."
  [attrs m f]
  (fn [& args]
    (map (partial replace-ids-in-tx attrs m)
         (apply f args))))

(defn clone-entities
  "Takes a datascript database and ids to be cloned and returns a transaction for creating shallow copies of the identified entities.
   All attributes, including forward references, are cloned.
   If reverse references should also be cloned, a collection of the relevant reverse attribute references has to be provided."
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
