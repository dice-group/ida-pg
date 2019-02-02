(ns librarian.helpers.map)

(defn merge-by-key
  [mergers default & maps]
  (let [[mergers default maps] (cond
                                 (fn? mergers)
                                 [{} mergers (cons default maps)]
                                 (map? default)
                                 [mergers
                                  (fn [& args] (last args))
                                  (cons default maps)]
                                 :else [mergers default maps])]
    (into {} (for [key (set (mapcat keys maps))]
               [key (->> maps
                         (keep key)
                         (apply (get mergers key default)))]))))

(defn update-keys
  [m keys f & args]
  (reduce (fn [m key]
            (if (contains? m key)
              (apply update m key f args)
              m))
          m keys))

(defn map-kv
  [f m]
  (into {} (for [e m] (f e))))

(defn map-v
  [f m]
  (into {} (for [[k v] m] [k (f v)])))

(defn keep-kv
  [f m]
  (into {} (for [e m :let [[k w] (f e)] :when w] [k w])))

(defn keep-v
  [f m]
  (into {} (for [[k v] m :let [w (f v)] :when w] [k w])))
