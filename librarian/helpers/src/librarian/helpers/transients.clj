(ns librarian.helpers.transients)

(defn into!
  [to from]
  (reduce conj! to from))
