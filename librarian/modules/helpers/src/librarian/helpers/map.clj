(ns librarian.helpers.map)

(defn merge-by-key
  [mergers default & maps]
  (let [[mergers default maps]
        (cond
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

(defn map-k
  [f m]
  (into {} (for [[k v] m] [(f k) v])))

(defn map-v
  [f m]
  (into {} (for [[k v] m] [k (f v)])))

(defn keep-kv
  [f m]
  (into {} (for [e m :let [[k w] (f e)] :when w] [k w])))

(defn keep-v
  [f m]
  (into {} (for [[k v] m :let [w (f v)] :when w] [k w])))

(defn get-or-fail
  ([map key]
   (get-or-fail map key (str "Could not get " key ".")))
  ([map key error-msg]
   (if (contains? map key)
     (get map key)
     (throw (Error. error-msg)))))

(defn all
  "Like take-while but reduces to nil if a non-matching item is found."
  ([] (all some?))
  ([pred]
   (fn [f]
     (fn
       ([] (f))
       ([res] (f res))
       ([res x] (if (pred x)
                  (f res x)
                  (reduced nil))))))
  ([pred coll]
   (when (every? pred coll) coll)))

(defn all-into
  "Like into but shortcircuits to return value nil if a nil element is inserted."
  ([to from]
   (all-into to identity some? from))
  ([to xform from]
   (all-into to xform some? from))
  ([to xform pred from]
   (let [xform (if (= xform identity) (all pred) (comp xform (all pred)))]
     (if (instance? clojure.lang.IEditableCollection to)
       (let [res (transduce xform conj! (transient to) from)]
         (when res (with-meta (persistent! res) (meta to))))
       (transduce xform conj to from)))))
