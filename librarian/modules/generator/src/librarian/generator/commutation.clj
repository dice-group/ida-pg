(ns librarian.generator.commutation)

(defmulti add-commute-id
  "Adds a commutative action identified by commute-id to a set of performed actions."
  (fn [type commutation commute-id] type)
  :default ::incomplete)

(defmulti add-commutation
  "Adds performed commutative actions to a set of already performed commutative actions."
  (fn [type commutations commutation] type)
  :default ::incomplete)

(defmulti tie-breaker
  (fn [type commute-id] type))

(defmethod tie-breaker :default
  [type commute-id]
  commute-id)

;; incomplete actions: all allowed permutations are equivalent:

(defmethod add-commute-id ::incomplete
  [type commutation commute-id]
  (conj (or commutation #{}) commute-id))

(defmethod add-commutation ::incomplete
  [type commutations commutation]
  (conj (or commutations #{}) commutation))

;; complete actions: all permutations are allowed and equivalent:

(defmethod add-commute-id ::complete
  [type commutation commute-id]
  (let [new-commutation (or commutation [])]
    ; actions that allow arbitrary application order are always applied in
    ; ascending commute-id order to reduce the search space:
    (if (neg? (compare (peek new-commutation) commute-id))
      (conj new-commutation commute-id)
      commutation)))
