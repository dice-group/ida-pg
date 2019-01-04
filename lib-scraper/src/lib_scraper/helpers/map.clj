(ns lib-scraper.helpers.map)

(defn merge-by-key
  [mergers default & maps]
  (let [[default maps] (if (map? default)
                         [(fn [& args] (last args))
                          (cons default maps)]
                         [default maps])]
    (into {} (for [key (set (mapcat keys maps))]
               [key (->> maps
                         (keep key)
                         (apply (get mergers key default)))]))))

(defn map-kv
  [f m]
  (into {} (for [[k v] m] [k (f v)])))

(defn keep-kv
  [f m]
  (into {} (for [[k v] m :let [w (f v)] :when w] [k w])))
