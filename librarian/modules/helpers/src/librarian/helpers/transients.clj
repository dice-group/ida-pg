(ns librarian.helpers.transients)

(defn into!
  "Like into but it keeps the target transient."
  ([to from]
   (reduce conj! to from))
  ([to xform from]
   (transduce xform conj! to from)))
