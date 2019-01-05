(ns lib-scraper.helpers.transaction
  (:require [datascript.core :as d])
  (:refer-clojure :exclude [merge]))

(defn add-attr
  [attr f]
  (fn [db id]
    [[:db/add id attr (f (d/entity db id))]]))

(defn merge
  [& fns]
  (let [fns (disj (set fns) nil)]
    (case (count fns)
      0 (constantly [])
      1 (first fns)
      (let [calls (mapv #(vector :db.fn/call %) fns)]
        (fn [db & args]
          (mapv #(into % args) calls))))))
