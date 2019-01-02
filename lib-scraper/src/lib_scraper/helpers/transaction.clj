(ns lib-scraper.helpers.transaction)

(defn merge-fns
  [fns]
  (case (count fns)
    0 (constantly [])
    1 (first fns)
    (let [calls (mapv #(vector :db.fn/call %) fns)]
      (fn [db & args]
        (mapv #(into % args) calls)))))

(defn merge-fn-maps
  [& maps]
  (into {} (for [key (set (mapcat keys maps))]
             [key (->> maps
                       (keep key) (set)
                       (merge-fns))])))
