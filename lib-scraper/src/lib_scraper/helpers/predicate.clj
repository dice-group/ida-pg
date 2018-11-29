(ns lib-scraper.helpers.predicate)

(defn p-and
  [& preds]
  (fn [& args]
    (loop [[pred & preds] preds]
      (if (and pred (apply pred args))
        (recur preds) (nil? pred)))))

(defn p-or
  [& preds]
  (fn [& args]
    (loop [[pred & preds] preds]
      (if (or (nil? pred) (apply pred args))
        true (recur preds)))))

(defn p-expire
  [limit]
  (let [count (atom 0)]
    (fn [& args] (<= (swap! count inc) limit))))

(defn p-log
  [f]
  (fn [& args]
    (println (apply f args))
    true))
