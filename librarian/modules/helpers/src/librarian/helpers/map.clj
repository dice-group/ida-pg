(ns librarian.helpers.map
  (:require [librarian.helpers.transients :as ht])
  (:refer-clojure :exclude [into]))

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
    (clojure.core/into {}
                       (for [key (set (mapcat keys maps))]
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
  (clojure.core/into {} (for [e m] (f e))))

(defn map-k
  [f m]
  (clojure.core/into {} (for [[k v] m] [(f k) v])))

(defn map-v
  [f m]
  (clojure.core/into {} (for [[k v] m] [k (f v)])))

(defn keep-kv
  [f m]
  (clojure.core/into {} (for [e m :let [[k w] (f e)] :when w] [k w])))

(defn keep-v
  [f m]
  (clojure.core/into {} (for [[k v] m :let [w (f v)] :when w] [k w])))

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

(defn into
  "Like clojure.core/into but supports reducing to non-collections."
  ([to from]
   (clojure.core/into to from))
  ([to xform from]
   (if (instance? clojure.lang.IEditableCollection to)
     (let [res (transduce xform conj! (transient to) from)]
       (when res (with-meta (persistent! res) (meta to))))
     (transduce xform conj to from))))

(defn update-into
  "Like into but applies (update to' k f v) for each key-value pair [k v] in from.
   clojure.core/into in contrast successively runs conj.
   Since update is used, to has to be associative."
  ([to f from]
   (update-into to identity f from))
  ([to xform f from]
   (if (instance? clojure.lang.IEditableCollection to)
     (let [res (transduce xform (fn
                                  ([m] m)
                                  ([m [k v]] (ht/update! m k f v)))
                          (transient to) from)]
       (when res (with-meta (persistent! res) (meta to))))
     (transduce xform (fn
                        ([m] m)
                        ([m [k v]] (update m k f v)))
                to from))))

(defn replace-if-some
  ([pred]
   (fn [f]
     (fn
       ([] (f))
       ([res] (f res))
       ([res x]
        (if-let [px (pred x)]
          (reduced px)
          (f res x))))))
  ([pred coll]
   (or (some pred coll) coll)))
