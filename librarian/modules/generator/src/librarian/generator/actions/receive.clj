(ns librarian.generator.actions.receive
  (:require [datascript.core :as d]
            [librarian.generator.query :as gq]
            [librarian.generator.cost :as gc]
            [librarian.model.concepts.typed :as typed]
            [librarian.model.concepts.data-receiver :as data-receiver]
            [librarian.model.concepts.call-parameter :as call-parameter]))

(defn receive-actions
  [{:keys [db]} flaw]
  (when-not (-> (d/entity db flaw) ::call-parameter/parameter :placeholder)
    (let [solutions (gq/compatibly-typed-sources db flaw)]
      (when (seq solutions)
        (let [{semantic-receivers :semantic, receivers :full} (gq/receivers db flaw)
              cost-evaluator (gc/semantic-cost-evaluator db (gq/semantic-types db flaw))]
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
                      {:type :receiver
                       :weight 1
                       :tx tx})))
                solutions))))))
