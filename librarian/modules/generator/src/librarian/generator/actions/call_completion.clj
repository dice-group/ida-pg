(ns librarian.generator.actions.call-completion
  (:require [datascript.core :as d]
            [clojure.math.combinatorics :as combo]
            [librarian.helpers.transients :refer [into!]]
            [librarian.generator.query :as gq]
            [librarian.model.concepts.callable :as callable]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.typed :as typed]))

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
                    {:type :call-completion
                     :cost 1
                     :tx tx}))))
         completions)))
