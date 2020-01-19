(ns librarian.helpers.transients
  "Helpers to work with transient collections.")

(defn into!
  "Like `into` but it keeps the target transient."
  ([to from]
   (reduce conj! to from))
  ([to xform from]
   (transduce xform conj! to from)))

(defn update!
  "Like `update` but for transients."
  ([m k f]
   (assoc! m k (f (get m k))))
  ([m k f x]
   (assoc! m k (f (get m k) x)))
  ([m k f x y]
   (assoc! m k (f (get m k) x y)))
  ([m k f x y z]
   (assoc! m k (f (get m k) x y z)))
  ([m k f x y z & more]
   (assoc! m k (apply f (get m k) x y z more))))
