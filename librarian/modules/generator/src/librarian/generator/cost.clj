(ns librarian.generator.cost
  (:require [datascript.core :as d]
            [librarian.generator.query :as gq]
            [librarian.model.concepts.callable :as callable]))

; minimal possible action cost (p=1/2):
(def c-min (Math/log 2))
; weight of param remove actions (max solution to 1/(2+x)^2 > x/(1+x)):
(def param-remove-weight 0.24697960371746706105)
; minimal possible action cost if param-removal is available but not chosen:
(def c-min-with-param-remove-option (Math/log (+ 2 param-remove-weight)))

(def ^:dynamic *min-semantic-weight* 1/50)
(def ^:dynamic *h-param-weight* 4)
(def ^:dynamic *h-call-weight* 3)
(def ^:dynamic *max-cost* (- (Math/log 1/100)))

(defn nlog
  "Negative logarithm."
  [^double p]
  (- (Math/log p)))

(defn log-factorial
  "Approximation of log(n!)."
  [^double n]
  (- (* n (Math/log n)) n))

(defn sematic-compatibility
  "The semantic compatibility of 'from to 'to.
   Only a mock implementation for now."
  [to-value from-value]
  (if (= from-value to-value)
    1
    0))

(defn max-compatibility
  [to-value from-values]
  (if (< (count to-value) 2)
    1
    (apply max 0 (map #(sematic-compatibility to-value %) from-values))))

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
          (+ *min-semantic-weight* (* (/ (apply + (vals compat)) (count compat))
                                      (- 1 *min-semantic-weight*))))))))

(defn costify-actions
  "Takes weighted actions, normalizes their weights and adds the normalized weight nlogs as costs."
  [actions]
  (let [weight-sum (Math/log (transduce (map :weight) + 1 actions))]
    (into []
          (comp (map #(assoc % :cost (- weight-sum (Math/log (:weight %)))))
                (filter #(<= (:cost %) *max-cost*)))
          actions)))

(defn match-completion-heuristic
  [db matches]
  (transduce (map (fn [match]
                    (let [call (:match match)
                          matched-params (count (::callable/parameter match))
                          params (count (d/datoms db :eavt call ::callable/parameter))]
                      (- params matched-params))))
             min Double/POSITIVE_INFINITY matches))

(defn cost-heuristic
  "Takes an incomplete CFG state and returns a lower bound on the remaining cost to find an executable CFG."
  [{:keys [db flaws source-candidates placeholder-matches]}]
  (let [min-receival-chain-cost (log-factorial (inc (count (:parameter flaws))))
        parameter-flaw-cost
        (transduce (map (fn [flaw]
                          (let [candidates (source-candidates flaw)
                                c-step (if (gq/optional-call-param? db flaw)
                                         c-min-with-param-remove-option
                                         c-min)]
                            (cond
                              ; placeholder completion + source addition cost:
                              (nil? candidates) (+ c-min c-step)
                              ; source addition cost:
                              (empty? candidates) c-step
                              :else 0))))
                   + min-receival-chain-cost (:parameter flaws))]
    ;(println "hcount"  (transduce (map #(-> % placeholder-matches meta :heuristic inc)) + 0 (:call flaws)))
    (+ (* parameter-flaw-cost *h-param-weight*)
       ; (1 call completion action) + (minimum number of added params actions):
       (* (transduce (map #(-> % placeholder-matches meta :heuristic)) + 1 (:call flaws))
          *h-call-weight* c-min))))
