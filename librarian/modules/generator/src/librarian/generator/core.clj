(ns librarian.generator.core
  (:require [datascript.core :as d]
            [clucie.core :as clucie]
            [clucie.store :as cstore]
            [clucie.document :as cdoc]
            [clucie.analysis :as canalysis]
            [clojure.tools.logging :as log]
            [clojure.data.priority-map :refer [priority-map]]
            [librarian.model.io.scrape :as scrape]
            [librarian.model.syntax :refer [instanciate instances->tx]]
            [librarian.model.concepts.callable :as callable]
            [librarian.model.concepts.result :as result]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-value :as call-value]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.basetype :as basetype]
            [librarian.model.concepts.goal-type :as goal-type]
            [librarian.model.concepts.semantic-type :as semantic-type]
            [librarian.model.concepts.named :as named]
            [librarian.model.concepts.typed :as typed]
            [librarian.generator.query :as gq]
            [repl-tools :as rt])
  (:import (org.apache.lucene.analysis.en EnglishAnalyzer)))

(defn scrape->docs
  [{:keys [db ecosystem]}]
  (->> db
       (d/q ecosystem
            '[:find ?c (distinct ?t) (distinct ?d) ?n
              :where [?c :type ?t]
                     [?c :description ?d]
                     [(get-else $ ?c ::named/name :unnamed) ?n]])
       (map (fn [[id type description name]]
              {:id id
               :name name
               ::clucie/raw-fields
               (concat (->> type
                            (mapcat ancestors)
                            (distinct)
                            (map #(cdoc/field :type (str %)
                                              {:indexed? true})))
                       (->> description
                            (map #(cdoc/field :description %
                                              {:indexed? true
                                               :tokenized? true}))))}))))

(defn add-scrape!
  [index scrape]
  (clucie/add! index (scrape->docs scrape) [:id]))

(defn scrape->index
  [scrape]
  (let [index (cstore/memory-store)]
    (add-scrape! index scrape)
    index))

(def analyzer (canalysis/analyzer-mapping (EnglishAnalyzer.)
                                          {:type (canalysis/keyword-analyzer)}))

(defn search
  [index max type description]
  (->> (clucie/search index
                      [{:type (str type)}
                       {:description description}]
                      max analyzer)
       (map (juxt #(Integer. (:id %))
                  (comp :score meta)))
       (into {})))

(defn initial-state
  [scrape goals]
  (let [concepts (concat (mapv (fn [goal]
                                 (instanciate call-parameter/call-parameter
                                   :datatype [#_(instanciate goal-type/goal-type
                                                  :id goal)
                                              (instanciate basetype/basetype
                                                :name "int")
                                              (instanciate semantic-type/semantic-type
                                                :key "description"
                                                :value "number of clusters")]))
                               goals)
                         [(instanciate call-value/call-value
                            :value "123"
                            :datatype [(instanciate semantic-type/semantic-type
                                         :key "description"
                                         :value "number of clusters")
                                       (instanciate basetype/basetype
                                         :name "str")])
                          (instanciate call-value/call-value
                            :value "456"
                            :datatype [(instanciate semantic-type/semantic-type
                                         :key "description"
                                         :value "iteration count")
                                       (instanciate basetype/basetype
                                         :name "str")])])]
    {:cost 0
     :db (d/db-with (:db scrape) (instances->tx concepts))}))

(defn flaws
  [state]
  (d/q '[:find [?flaw ...]
         :in $ %
         :where (type ?flaw ::call-parameter/call-parameter)
                (not [?flaw ::call-parameter/receives ?value])]
       (:db state) gq/rules))

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

(defn receive-actions
  [state flaw]
  (let [db (:db state)
        solutions
        (d/q '[:find [?solution ...]
               :in $ % ?flaw
               :where (or (type ?solution ::call-result/call-result)
                          (type ?solution ::call-value/call-value))
                      (typed-compatible ?solution ?flaw)
                      (not (depends-on ?solution ?flaw))]
             db gq/rules flaw)
        cost-evaluator (semantic-cost-evaluator db (gq/semantic-types db flaw))]
    (when (seq solutions)
      (let [semantic-receivers
            (d/q '[:find (distinct ?receiver) .
                   :in $ % ?flaw
                   :where (receives-semantic ?receiver ?flaw)]
                 db gq/rules flaw)
            receivers
            (d/q '[:find (distinct ?receiver) .
                   :in $ % ?flaw [?receiver ...]
                   :where (receives ?receiver ?flaw)]
                 db gq/rules flaw semantic-receivers)]
        (keep (fn [solution]
                (let [types (gq/types db solution)
                      semantic-types (keep (fn [{:keys [db/id semantic]}] (when semantic id)) types)
                      semantic-cost (cost-evaluator semantic-types)
                      tx (mapcat (fn [{:keys [db/id semantic]}]
                                   (map (fn [r] [:db/add r ::typed/datatype id])
                                        (if semantic semantic-receivers receivers)))
                                 types)
                      tx (conj tx [:db/add flaw ::call-parameter/receives solution])]
                  (println solution flaw semantic-cost)
                  (when (< semantic-cost Double/POSITIVE_INFINITY)
                    {:cost semantic-cost, :tx tx})))
              solutions)))))

(defn call-actions
  [state flaw]
  (let [db (:db state)
        candidates
        (d/q '[:find ?callable ?result
               :in $ % ?flaw
               :where [?flaw ::typed/datatype ?ft]
                      [?result ::typed/datatype ?ft]
                      [?callable ::callable/result ?result]]
             db gq/rules flaw)
        callables (reduce (fn [callables [callable result]]
                            (if (and (not (contains? callables callable))
                                     (gq/typed-compatible? db result flaw))
                              (conj callables callable)
                              callables))
                          #{} candidates)
        callables (map (partial d/pull db [:db/id
                                           ::typed/datatype
                                           ::callable/parameter
                                           ::callable/result])
                       callables)]
    (map (fn [callable]
           {:cost (-> callable ::callable/parameter count inc)
            :tx [{:type ::call/call
                  ::call/callable (:db/id callable)
                  ::call/parameter
                  (map (fn [param]
                         {:type ::call-parameter/call-parameter
                          ::call-parameter/parameter (:db/id param)
                          ::typed/datatype (map :db/id (::typed/datatype param))})
                       (::callable/parameter callable))
                  ::call/result
                  (map (fn [result]
                         {:type ::call-result/call-result
                          ::call-result/result (:db/id result)
                          ::typed/datatype (map :db/id (::typed/datatype result))})
                       (::callable/result callable))}]})
         callables)))

(defn apply-action
  [state action]
  {:predecessor state
   :cost (+ (:cost state) (:cost action))
   :db (d/db-with (:db state) (:tx action))})

(defn successors
  [state]
  (println "succ")
  (let [state-flaws (flaws state)]
    (if (empty? state-flaws)
      :done
      (let [actions (mapcat #(receive-actions state %) state-flaws)
            actions (if (empty? actions)
                      (mapcat #(call-actions state %) state-flaws)
                      actions)
            actions (sort-by :cost actions)]
        (map (partial apply-action state) actions)))))

(defn search-state
  [state]
  {:queue (priority-map state (:cost state))})

(def initial-search-state (comp search-state initial-state))

(defn continue-search
  [{:keys [queue done failed] :as search-state}]
  (if (or done failed)
    search-state
    (if (empty? queue)
      {:failed true}
      (let [[state] (peek queue)
            state-successors (successors state)]
        (if (= state-successors :done)
          {:done true, :goal state}
          {:queue (into (pop queue)
                        (map (fn [succ] [succ (:cost succ)]))
                        state-successors)})))))

(try
  (let [scrape (scrape/read-scrape "libs/scikit-learn-cluster")
        search-state (initial-search-state scrape [:labels])
        succs (iterate continue-search search-state)]
    (time (rt/show-state (some :goal (take 100 succs))
                         :show-patterns true)))
  (catch Exception e
    (.println *err* e)))
