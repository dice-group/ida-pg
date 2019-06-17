(ns librarian.generator.cost
  (:require [librarian.generator.query :as gq]))

; minimal possible action cost (p=1/2):
(def c-min (Math/log 2))
; weight of param remove actions (max solution to 1/(2+x)^2 > x/(1+x)):
(def param-remove-weight 0.24697960371746706105)
; minimal possible action cost if param-removal is available but not chosen:
(def c-min-with-param-remove-option (Math/log (+ 2 param-remove-weight)))

(def ^:dynamic *h-param-weight* 3)
(def ^:dynamic *h-call-weight* 2)

(defn nlog
  "Negative logarithm."
  [^double p]
  (- (Math/log p)))

(defn sematic-compatibility
  "The semantic compatibility of 'from to 'to.
   Only a mock implementation for now."
  [to-value from-value]
  (println "compat" to-value from-value)
  (if (= from-value to-value)
    1
    0))

(defn max-compatibility
  [to-value from-values]
  (apply max 0 (map #(sematic-compatibility to-value %) from-values)))

(defn semantic-compatibility-evaluator
  [db to-types]
  (let [to-types (map (partial gq/type-semantics db) to-types)]
    (if (zero? (count to-types))
      (constantly 1)
      (fn [from-types]
        (let [from-types (group-by :key (map (partial gq/type-semantics db) from-types))
              general-values (map :value (from-types nil))
              compat
              (reduce (fn [compat {:keys [key value]}]
                        (let [key-values (map :value (from-types key))
                              current-compat (compat key 0)
                              general-compat (when key (/ (max-compatibility value general-values) 2))
                              key-compat (max-compatibility value key-values)]
                          (assoc! compat key (max current-compat general-compat key-compat))))
                      (transient {nil 0}) to-types)
              compat (persistent! compat)]
          (+ 1/2 (/ (apply + (vals compat)) (dec (count compat)) 2)))))))

(defn costify-actions
  "Takes weighted actions, normalizes their weights and adds the normalized weight nlogs as costs."
  [actions]
  (let [weight-sum (Math/log (transduce (map :weight) + 1 actions))]
    (map #(assoc % :cost (- weight-sum (Math/log (:weight %))))
         actions)))

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
