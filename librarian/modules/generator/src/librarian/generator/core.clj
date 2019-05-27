(ns librarian.generator.core
  (:require [datascript.core :as d]
            [clucie.core :as clucie]
            [clucie.store :as cstore]
            [clucie.document :as cdoc]
            [clucie.analysis :as canalysis]
            [clojure.tools.logging :as log]
            [clojure.core.memoize :as memo]
            [clojure.data.priority-map :refer [priority-map]]
            [clojure.math.combinatorics :as combo]
            [librarian.helpers.transients :refer [into!]]
            [librarian.helpers.transaction :as tx]
            [librarian.model.concepts.callable :as callable]
            [librarian.model.concepts.parameter :as parameter]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.data-receiver :as data-receiver]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.role-type :as role-type]
            [librarian.model.concepts.snippet :as snippet]
            [librarian.model.concepts.named :as named]
            [librarian.model.concepts.namespace :as namespace]
            [librarian.model.concepts.typed :as typed]
            [librarian.generator.query :as gq])
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
  [scrape tx]
  {:cost 0
   :db (d/db-with (:db scrape) tx)
   :placeholder-matches (memo/memo #'gq/placeholder-matches)})

(defn flaws
  [{:keys [db]}]
  (let [global-type-finder (comp (gq/types->instances db)
                                 (remove #(d/datoms db :avet ::snippet/contains %)))]
    {:parameter (into []
                      (comp global-type-finder
                            (remove #(d/datoms db :eavt % ::data-receiver/receives)))
                      [::call-parameter/call-parameter])
     :call (into []
                 (comp global-type-finder
                       (remove (fn [id]
                                 (if-let [[{callable :v}] (d/datoms db :eavt id ::call/callable)]
                                   (not (:v (first (d/datoms db :eavt callable :placeholder))))))))
                 [::call/call])}))

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
  [{:keys [db]} flaw]
  (when-not (-> (d/entity db flaw) ::call-parameter/parameter :placeholder)
    (let [solutions (gq/compatibly-typed-sources db flaw)
          cost-evaluator (semantic-cost-evaluator db (gq/semantic-types db flaw))]
      (when (seq solutions)
        (let [{semantic-receivers :semantic, receivers :full} (gq/receivers db flaw)]
          (keep (fn [solution]
                  (let [types (gq/types db solution)
                        semantic-types (keep (fn [{:keys [db/id semantic]}] (when semantic id)) types)
                        semantic-cost (cost-evaluator semantic-types)
                        tx (mapcat (fn [{:keys [db/id semantic]}]
                                     (map (fn [r] [:db/add r ::typed/datatype id])
                                          (if semantic semantic-receivers receivers)))
                                   types)
                        tx (conj tx [:db/add flaw ::data-receiver/receives solution])]
                    (when (< semantic-cost Double/POSITIVE_INFINITY)
                      {:cost semantic-cost, :tx tx})))
                solutions))))))

(defn call-actions
  [{:keys [db]} flaw]
  (let [candidates
        (d/q '[:find ?callable ?result
               :in $ ?flaw
               :where [?flaw ::typed/datatype ?ft]
                      [?result ::typed/datatype ?ft]
                      [?callable ::callable/result ?result]]
             db flaw)
        callables (reduce (fn [callables [callable result]]
                            (if (and (not (contains? callables callable))
                                     (gq/typed-compatible? db result flaw))
                              (conj callables callable)
                              callables))
                          #{} candidates)
        callables (map (partial d/pull db [:db/id
                                           ::named/name
                                           ::callable/parameter
                                           ::callable/result])
                       callables)]
    (map (fn [callable]
           (let [param-map (into {}
                                 (map-indexed (fn [i {:keys [db/id] :as param}]
                                                [id {:db/id (- (inc i))
                                                     :type ::call-parameter/call-parameter
                                                     ::call-parameter/parameter id
                                                     ::typed/datatype (map :db/id (::typed/datatype param))}]))
                                 (::callable/parameter callable))]
             {:cost (-> callable ::callable/parameter count inc)
              :tx (conj (vec (vals param-map))
                        {:type ::call/call
                         ::call/callable (:db/id callable)
                         ::call/parameter (map (comp - inc) (range (count param-map)))
                         ::call/result
                         (map (fn [result]
                                (let [call-result
                                      {:type ::call-result/call-result
                                       ::call-result/result (:db/id result)
                                       ::typed/datatype (map :db/id (::typed/datatype result))
                                       ::data-receiver/receives-semantic
                                       (keep (comp :db/id param-map :db/id)
                                             (::data-receiver/receives-semantic result))}
                                      rec-id (some-> result ::data-receiver/receives :db/id param-map :db/id)]
                                  (if rec-id
                                    (assoc call-result ::data-receiver/receives rec-id)
                                    call-result)))
                              (::callable/result callable))})}))
         callables)))

(defn snippet->action
  [db snippet]
  (let [tx (tx/clone-entities db (map :v (d/datoms db :eavt snippet ::snippet/contains))
                              [::namespace/member])]
    {:cost 1
     :tx tx}))

(defn snippet-actions
  [{:keys [db]} flaw]
  (sequence (comp (map :e)
                  (remove #(empty? (gq/compatibly-typed-sources db flaw %)))
                  (map #(snippet->action db %)))
            (d/datoms db :avet :type ::snippet/snippet)))

(defn- all-valid-call-io-match-combos
  [matched-entities]
  (into []
        (comp (filter #(apply distinct? (map :placeholder %)))
              (map #(into {} (map (fn [p] [(:match p)
                                           (:placeholder p)]))
                          %)))
        (apply combo/cartesian-product matched-entities)))

(defn- io-conts->tx
  "Returns a transducer that maps a series of io-container ids (params or results of callables) to a datascript tx.
   This tx will add the given params to a given call.
   Preexisting io-containers in call are detected via lookup-io.
   If an io-container is found in call, it will be replaced; if not, a new io-container will be created."
  [call-io-type call-io->io-attr call->call-io-attr]
  (fn [call io->call-io-data]
    (mapcat (fn [{io :db/id, types :types}]
              (if-some [{call-io :db/id, {:keys [semantic full]} :receivers} (io->call-io-data io)]
                (into [[:db/add call-io call-io->io-attr io]]
                      (mapcat #(eduction (map (fn [rec] [:db/add rec ::typed/datatype (:db/id %)]))
                                         (if (:semantic %) semantic full)))
                      types)
                (let [call-io (d/tempid nil)]
                  [{:db/id call-io
                    :type call-io-type
                    call-io->io-attr io
                    ::typed/datatype (map :db/id types)}
                   [:db/add call call->call-io-attr call-io]]))))))

(def param->tx (io-conts->tx ::call-parameter/call-parameter
                             ::call-parameter/parameter
                             ::call/parameter))

(def result->tx (io-conts->tx ::call-result/call-result
                              ::call-result/result
                              ::call/result))

(defn call->io-conts
  [db call io-type]
  (mapv (fn [p]
          (let [pid (:v p)]
            {:db/id pid
             :types (gq/types db pid)}))
       (d/datoms db :eavt call io-type)))

(defn call-completion-actions
  [{:keys [db placeholder-matches]} flaw]
  (let [callable (:v (first (d/datoms db :eavt flaw ::call/callable)))
        e (d/entity db flaw)
        p->cp (into {} (map (fn [p] [(-> p ::call-parameter/parameter :db/id)
                                     (let [id (:db/id p)]
                                       {:db/id id
                                        :receivers (gq/receivers db id)})]))
                    (::call/parameter e))
        r->cr (into {} (map (fn [r] [(-> r ::call-result/result :db/id)
                                     (let [id (:db/id r)]
                                       {:db/id id
                                        :receivers (gq/receivers db id)})]))
                    (::call/result e))
        completions (placeholder-matches db callable)]
    (mapcat (fn [{match :match
                  matched-params ::callable/parameter
                  matched-results ::callable/result}]
              (let [params (call->io-conts db match ::callable/parameter)
                    results (call->io-conts db match ::callable/result)
                    params-combos (all-valid-call-io-match-combos matched-params)
                    results-combos (all-valid-call-io-match-combos matched-results)]
                (for [params-combo params-combos
                      results-combo results-combos]
                  (let [tx (-> (transient [[:db/add flaw ::call/callable match]])
                               (into! (param->tx flaw (comp p->cp params-combo))
                                      params)
                               (into! (result->tx flaw (comp r->cr results-combo))
                                      results)
                               (persistent!))]
                    {:cost 0
                     :tx tx}))))
         completions)))

(defn param-remove-actions
  [{:keys [db]} flaw]
  (when (-> (d/entity db flaw) ::call-parameter/parameter ::parameter/optional)
    [{:cost 1
      :tx [[:db.fn/retractEntity flaw]]}]))

(defn apply-action
  [state action]
  {:predecessor state
   :cost (+ (:cost state) (:cost action))
   :db (d/db-with (:db state) (:tx action))
   :placeholder-matches (:placeholder-matches state)})

(defn successors
  [state]
  (let [{p-flaws :parameter, c-flaws :call} (flaws state)]
    (println "flaws:" p-flaws c-flaws)
    (if (and (empty? p-flaws) (empty? c-flaws))
      :done
      (let [actions (transient [])
            actions (into! actions (mapcat #(receive-actions state %)) p-flaws)
            actions (if (zero? (count actions))
                      (into! actions
                             (mapcat #(concat (call-actions state %)
                                              (snippet-actions state %)))
                             p-flaws)
                      actions)
            actions (into! actions (mapcat #(param-remove-actions state %)) p-flaws)
            actions (into! actions (mapcat #(call-completion-actions state %)) c-flaws)]
        (map (partial apply-action state) (persistent! actions))))))

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
