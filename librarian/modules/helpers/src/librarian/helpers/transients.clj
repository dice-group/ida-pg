(ns librarian.helpers.transients)

(defn into!
  ([to from]
   (reduce conj! to from))
  ([to xform from]
   (transduce xform conj! to from)))
