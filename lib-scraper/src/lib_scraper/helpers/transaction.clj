(ns lib-scraper.helpers.transaction
  (:refer-clojure :exclude [merge]))

(defn merge
  [& fns]
  (let [fns (disj (set fns) nil)]
    (case (count fns)
      0 (constantly [])
      1 (first fns)
      (let [calls (mapv #(vector :db.fn/call %) fns)]
        (fn [db & args]
          (mapv #(into % args) calls))))))
