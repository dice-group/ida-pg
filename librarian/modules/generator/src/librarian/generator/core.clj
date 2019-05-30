(ns librarian.generator.core
  (:require [datascript.core :as d]
            [clojure.core.memoize :as memo]
            [clojure.data.priority-map :refer [priority-map]]
            [librarian.helpers.transients :refer [into!]]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.data-receiver :as data-receiver]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.snippet :as snippet]
            [librarian.generator.query :as gq]
            [librarian.generator.actions.call :refer [call-actions]]
            [librarian.generator.actions.receive :refer [receive-actions]]
            [librarian.generator.actions.snippet :refer [snippet-actions]]
            [librarian.generator.actions.param-remove :refer [param-remove-actions]]
            [librarian.generator.actions.call-completion :refer [call-completion-actions]])
  (:import (org.apache.lucene.analysis.en EnglishAnalyzer)))

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
