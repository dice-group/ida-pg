(ns lib-scraper.helpers.transaction
  (:require [datascript.core :as d]
            [datascript.db :as db])
  (:refer-clojure :exclude [merge]))

(defn add-attr
  [attr f]
  (fn [db id]
    (if-let [v (f (d/entity db id))]
      [[:db/add id attr v]]
      [])))

(defn merge
  [& fns]
  (let [fns (disj (set fns) nil)]
    (case (count fns)
      0 (constantly [])
      1 (first fns)
      (let [calls (mapv #(vector :db.fn/call %) fns)]
        (fn [db & args]
          (mapv #(into % args) calls))))))

(defn tempid?
  ^Boolean [x]
  (or (and (number? x) (neg? x)) (string? x)))

(defn- map-id
  [m db tx]
  (when (sequential? tx)
    (let [[op e a v] tx
          es (m e e)
          vs (if (db/ref? db a) (m v v) [v])]
      (println e [op es a v])
      (for [e es, v vs]
        [op e a v]))))

(defn map-ids
  [m f]
  (fn [db & args]
    (mapcat (partial map-id m db)
         (apply f db args))))
