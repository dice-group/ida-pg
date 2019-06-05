(ns librarian.generator.core
  (:require [datascript.core :as d]
            [clojure.core.memoize :as memo]
            [clojure.data.priority-map :refer [priority-map]]
            [librarian.helpers.transients :refer [into! update!]]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.data-receiver :as data-receiver]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.snippet :as snippet]
            [librarian.generator.query :as gq]
            [librarian.generator.cost :as gc]
            [librarian.generator.actions.call :refer [call-actions]]
            [librarian.generator.actions.receive :refer [receive-actions]]
            [librarian.generator.actions.snippet :refer [snippet-actions]]
            [librarian.generator.actions.param-remove :refer [param-remove-actions]]
            [librarian.generator.actions.call-completion :refer [call-completion-actions]])
  (:import (org.apache.lucene.analysis.en EnglishAnalyzer)))

(def *sid (atom 0))

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

(defn add-removed!
  [{:keys [removed] :as state} remove]
  (if (some? remove)
    (if removed
      (when (< (peek removed) (first remove))
        (-> state
            (assoc! :tie-breaker (first remove))
            (assoc! :removed (into removed remove))))
      (-> state
          (assoc! :tie-breaker (first remove))
          (assoc! :removed (vec remove))))
    (assoc! state :tie-breaker Double/POSITIVE_INFINITY)))

(defn add-source-candidates!
  [{:keys [db flaws] :as state} add]
  (if add
    (assoc! state :source-candidates
            (into {}
                  (comp (filter #(gq/fillable-call-param? db %))
                        (map (fn [flaw] [flaw (gq/compatibly-typed-sources db flaw)])))
                  (:parameter flaws)))
    state))

(defn apply-action
  [state action]
  (when-let [new-state (add-removed! (transient state) (:remove action))]
    (as-> new-state $
          (assoc! $ :id (swap! *sid inc))
          (assoc! $ :predecessor state)
          (update! $ :db d/db-with (:tx action))
          (update! $ :cost + (:cost action))
          (assoc! $ :flaws (flaws $))
          (add-source-candidates! $ (:add action))
          (assoc! $ :heuristic (+ (:cost $) (gc/cost-heuristic $)))
          (persistent! $))))

(defn initial-state
  [scrape tx]
  (apply-action {:cost 0
                 :db (:db scrape)
                 :placeholder-matches (memo/memo #'gq/placeholder-matches)}
                {:cost 0, :tx tx, :add true}))

(defn successors
  [state]
  (let [{p-flaws :parameter, c-flaws :call} (:flaws state)]
    (println "id:" (:id state) "pred:" (:id (:predecessor state))
             "c:" (:cost state) "h:" (:heuristic state) "flaws:" p-flaws c-flaws)
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
            actions (into! actions (mapcat #(call-completion-actions state %)) c-flaws)
            actions (persistent! actions)]
        (keep (partial apply-action state)
              (gc/costify-actions actions))))))

(defn search-state
  [state]
  {:queue (priority-map state (:heuristic state))})

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
                        (map (fn [succ] [succ [(:heuristic succ) (:tie-breaker succ)]]))
                        state-successors)})))))
