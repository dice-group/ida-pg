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
  (let [weight-sum (Math/log (transduce (map :weight) + 1 actions))]
    (map #(assoc % :cost (- weight-sum (Math/log (:weight %))))
         actions)))

; minimal possible action cost (p=1/2):
(def c-min (Math/log 2))
; weight of param remove actions (max solution to 1/(2+x)^2 > x/(1+x)):
(def param-remove-weight 0.24697960371746706105)
; minimal possible action cost if param-removal is available but not chosen:
(def c-min-with-param-remove-option (Math/log (+ 2 param-remove-weight)))

(def ^:dynamic *h-param-weight* 3)
(def ^:dynamic *h-call-weight* 2)

(defn cost-heuristic
  "Takes an incomplete CFG state and returns a lower bound on the remaining cost to find an executable CFG."
  [{:keys [db flaws source-candidates]}]
  (let [parameter-flaw-cost
        (transduce (map (fn [flaw]
                          (let [candidates (source-candidates flaw)
                                c-step (if (gq/optional-call-param? db flaw)
                                         c-min-with-param-remove-option
                                         c-min)]
                            (+ (if (nil? candidates) c-min 0) ; placeholder completion cost
                               (if (empty? candidates) c-step 0) ; source addition cost
                               c-step)))) ; receive cost
                   + 0 (:parameter flaws))]
    (+ (* parameter-flaw-cost *h-param-weight*)
       (-> flaws :call count (* *h-call-weight* c-min)))))
