(ns librarian.helpers.predicate
  "A collection of functions to work with boolean predicates."
  (:require [clojure.tools.logging :as log]))

(defn p-and
  "Returns a predicate that is the conjunction of the given predicates."
  [& preds]
  (fn [& args]
    (loop [[pred & preds] preds]
      (if (nil? pred) true
        (if (apply pred args)
          (recur preds) false)))))

(defn p-or
  "Returns a predicate that is the disjunction of the given predicates."
  [& preds]
  (fn [& args]
    (loop [[pred & preds] preds]
      (if (nil? pred) false
        (if (apply pred args)
          true (recur preds))))))

(defn p-expire
  "Returns a stateful predicate that is true for the first `limit` invokations and false afterwards.
   If `limit` is `-1` it always returns true."
  [limit]
  (if (= limit -1)
    (constantly true)
    (let [count (atom 0)]
      (fn [& args] (<= (swap! count inc) limit)))))

(defn p-log
  "Returns a predicate of flexible arity that is always true but logs its arguments when called.
   The arguments are formatted by the formatter `f` before logging."
  [f]
  (fn [& args]
    (log/info (apply f args))
    true))

(defn parse-base
  [preds expr]
  (cond
    (list? expr)
    (let [[head & rest] expr]
      (if-let [pred (preds head)]
        (apply pred (map (partial parse-base preds) rest))
        (throw (Exception. (str "Unknown predicate '" head "'.")))))
    (symbol? expr) (throw (Exception. "Predicate expression must not contain free variables."))
    :else expr))

(defn parse
  "Parses a given syntax form representing a boolean formula and transforms it into a callable predicate function.
   The logical operators `and`, `or` and `not` may be used.
   Additionally the predicate `expire` is available.
   All other predicates that might occur in `expr` need to be defined in `preds` which is a map from predicate symbols to predicate functions."
  [preds expr]
  (parse-base (merge {'not not
                      'and p-and
                      'or p-or
                      'expire p-expire}
                     preds)
              expr))
