(ns librarian.generator.actions.call
  (:require [datascript.core :as d]
            [librarian.generator.query :as gq]
            [librarian.model.concepts.typed :as typed]
            [librarian.model.concepts.data-receiver :as data-receiver]
            [librarian.model.concepts.callable :as callable]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.named :as named]))

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
             {:type :call
              :weight 1/2
              :add true
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
