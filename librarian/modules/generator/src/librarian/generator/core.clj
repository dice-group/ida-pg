(ns librarian.generator.core
  (:require [datascript.core :as d]
            [clojure.data.priority-map :refer [priority-map]]
            [librarian.helpers.transients :refer [into! update!]]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.generator.query :as gq]
            [librarian.generator.cost :as gc]
            [librarian.generator.commutation :as gcomm]
            [librarian.generator.actions.call :refer [call-actions]]
            [librarian.generator.actions.receive :refer [receive-actions]]
            [librarian.generator.actions.snippet :refer [snippet-actions]]
            [librarian.generator.actions.param-remove :refer [param-remove-actions]]
            [librarian.generator.actions.call-completion :refer [call-completion-actions]]))

(def ^:dynamic *sid (atom 0))

(defn flaws
  [{:keys [db]}]
  (let [global-type-finder (comp (gq/types->instances db)
                                 (remove #(gq/containing-snippet db %)))]
    {:parameter (into []
                      (comp global-type-finder
                            (remove #(gq/receives? db %)))
                      [::call-parameter/call-parameter])
     :call (into []
                 (comp global-type-finder
                       (remove (fn [id]
                                 (if-let [[{callable :v}] (d/datoms db :eavt id ::call/callable)]
                                   (not (gq/placeholder? db callable))))))
                 [::call/call])}))

(defn update-commutations!
  [{:keys [seen-commutations commutation] :as state} {:keys [type commute-id]}]
  (if (some? commute-id)
    (let [new-commutation (update commutation type
                                  #(gcomm/add-commute-id type %1 %2)
                                  commute-id)
          [old new] (swap-vals! seen-commutations update type
                                #(gcomm/add-commutation type %1 %2)
                                new-commutation)]
      (when (not= old new)
        (assoc! state
                :commutation new-commutation
                :tie-breaker (gcomm/tie-breaker type commute-id))))
    (assoc! state
            :seen-commutations (atom {})
            :commutation {})))

(defn add-source-candidates!
  [{:keys [db flaws] :as state} {add :add, remove-ids :remove}]
  (let [cands (if add
                (into {}
                      (comp (filter #(gq/fillable-call-param? db %))
                            (map (fn [flaw] [flaw (gq/compatibly-typed-sources db flaw)])))
                      (:parameter flaws))
                (if (seq remove-ids)
                  (let [remove-ids (set remove-ids)]
                    (into {}
                          (keep (fn [[k :as kv]]
                                  (when-not (contains? remove-ids k) kv)))
                          (:source-candidates state)))
                  (:source-candidates state)))]
    (assoc! state :source-candidates cands)))

(defn add-placeholder-matches!
  [{:keys [db flaws placeholder-matches] :or {placeholder-matches {}} :as state} action]
  (if (:add action)
    (assoc! state :placeholder-matches
            (into placeholder-matches
                  (comp (remove placeholder-matches)
                        (map (fn [call] [call (gq/placeholder-matches db (gq/callable db call))])))
                  (:call flaws)))
    state))

(defn apply-action
  [state action]
  (when (< (:cost action) Double/POSITIVE_INFINITY)
    (when-let [new-state (update-commutations! (transient state) action)]
      (as-> new-state $
            (assoc! $ :id (swap! *sid inc))
            (assoc! $ :predecessor state)
            (update! $ :db d/db-with (:tx action))
            (update! $ :cost + (:cost action))
            (assoc! $ :flaws (flaws $))
            (add-source-candidates! $ action)
            (add-placeholder-matches! $ action)
            (assoc! $ :heuristic (+ (:cost $) (gc/cost-heuristic $)))
            (persistent! $)))))

(defn initial-state
  [scrape tx]
  (apply-action {:cost 0
                 :db (:db scrape)}
                {:cost 0, :tx tx, :add true}))

(defn actions
  [state]
  (let [{p-flaws :parameter, c-flaws :call} (:flaws state)]
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
        (gc/costify-actions actions)))))

(defn successors
  [state]
  (let [acts (actions state)]
    (if (= acts :done)
      :done
      (keep #(apply-action state %) acts))))

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
