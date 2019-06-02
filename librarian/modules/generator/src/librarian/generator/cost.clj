(ns librarian.generator.cost
  (:require [librarian.generator.query :as gq]))

(defn nlog
  "Negative logarithm."
  [^double p]
  (- (Math/log p)))

(defn nlog-distance
  "The negative logarithm of the semantic compatibility of 'from to 'to.
   Only a mock implementation for now."
  [to-value from-value]
  (if (= from-value to-value)
    0
    Double/POSITIVE_INFINITY))

(defn min-nlog-distance
  [to-value from-values]
  (apply min Double/POSITIVE_INFINITY
         (map (partial nlog-distance to-value) from-values)))

(defn semantic-cost-evaluator
  [db to-types]
  (let [to-types (map (partial gq/type-semantics db) to-types)]
    (fn [from-types]
      (let [from-types (group-by :key (map (partial gq/type-semantics db) from-types))
            general-values (map :value (from-types nil))
            costs
            (reduce (fn [costs {:keys [key value]}]
                      (let [key-values (map :value (from-types key))
                            current-cost (costs key Double/POSITIVE_INFINITY)
                            general-cost (inc (min-nlog-distance value (when key general-values)))
                            key-cost (min-nlog-distance value key-values)]
                        (assoc costs key (min current-cost general-cost key-cost))))
                    {} to-types)]
        (apply + (vals costs))))))

(defn costify-actions
  "Takes weighted actions, normalizes their weights and adds the normalized weight nlogs as costs."
  [actions]
  (let [weight-sum (Math/log (inc (transduce (map :weight) + actions)))]
    (map #(assoc % :cost (- weight-sum (Math/log (:weight %))))
         actions)))

(def h-normalize (Math/log 2))
(def ^:dynamic *h-param-weight* 2)
(def ^:dynamic *h-call-weight* 2)

(defn cost-heuristic
  [{:keys [flaws]}]
  (+ (-> flaws :parameter count (* *h-param-weight* h-normalize))
     (-> flaws :call count (* *h-call-weight* h-normalize))))
