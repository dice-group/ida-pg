(ns librarian.generator.cost
  "Implementation of the cost and heuristic measures used by the A* search through the CFG space."
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

(defn semantic-compatibility
  "The semantic compatibility of `from` to `to`.
   Only a mock implementation for now."
  [to-value from-value]
  (if (= from-value to-value)
    1
    0))

(defn max-compatibility
  "Takes one semantic type `to-value` of a receiver and a collection `from-values` of the semantic types of some source.
   Returns the maximum semantic compatibility of some semantic source type to the receiving type."
  [to-value from-values]
  (if (< (count to-value) 2)
    1
    (apply max 0 (map #(semantic-compatibility to-value %) from-values))))

(defn semantic-compatibility-evaluator
  "Takes a database and the ids of semantic datatypes of some receiver.
   Returns a so called semantic compatibility evaluator (SCE) for that receiver.
   An SCE is a function that takes a collection of the ids of the semantic datatypes of a candidate source for the receiver.
   The SCE returns a semantic compatibility score representing how well the candidate source fits the receiver.
   The score is in `[0, 1]` where a higher score represents higher semantic compatibility."
  [db to-types]
  (let [to-types (map (partial gq/type-semantics db) to-types)]
    (if (zero? (count to-types))
      ; If the receiver has no semantic requirements, everything is considered to be fully semantically compatible to it:
      (constantly 1)
      ; Else:
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
          (+ *min-semantic-weight* ; Always add some minimum semantic compatibility as a form of smoothing.
             (* (/ (apply + (vals compat)) (count compat)) ; Average compatibility of the source to each semantic type of the receiver.
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
  "A cost measure for how well a collection of matches fit some placeholder callable.
   Counts the number of additional parameter flaws that will be introduced by applying each of the given matches.
   Returns the minimum number of added parameters introduced by the matches.
   Used in the A* search to lower-bound the expected cost caused by some placeholder completion."
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
    (+ (* parameter-flaw-cost *h-param-weight*)
       ; (1 call completion action) + (minimum number of added params actions):
       (* (transduce (map #(-> % placeholder-matches meta :heuristic)) + 1 (:call flaws))
          *h-call-weight* c-min))))
