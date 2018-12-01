(ns lib-scraper.helpers.predicate)

(defn p-and
  [& preds]
  (fn [& args]
    (loop [[pred & preds] preds]
      (if (nil? pred) true
        (if (apply pred args)
          (recur preds) false)))))

(defn p-or
  [& preds]
  (fn [& args]
    (loop [[pred & preds] preds]
      (if (nil? pred) false
        (if (apply pred args)
          true (recur preds))))))

(defn p-expire
  [limit]
  (if (= limit -1)
    (constantly true)
    (let [count (atom 0)]
      (fn [& args] (<= (swap! count inc) limit)))))

(defn p-log
  [f]
  (fn [& args]
    (println (apply f args))
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
  [preds expr]
  (parse-base (merge {'not not
                      'and p-and
                      'or p-or
                      'expire p-expire}
                     preds)
              expr))
