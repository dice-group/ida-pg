(ns librarian.generator.generate
  "Implementation of the code generator's A* search."
  (:require [datascript.core :as d]
            [clojure.data.priority-map :refer [priority-map]]
            [librarian.helpers.transients :refer [into! update!]]
            [librarian.helpers.map :refer [update-into]]
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
  "Returns the flaws of a given state.
   There are parameter flaws for parameters without a value and call flaws for calls with a placeholder callable that still needs to concretized."
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
  "Updates the set of tried combinations of commutative actions.
   Takes a transient state and an action.
   If the application of the action results in a state that has already been tried, `nil` is returned.
   If an untried state is reached, the given state is returned with the newly reached commutation combination marked as tried."
  [{:keys [seen-commutations commutation] :as state} {:keys [type commute-id]}]
  (if (some? commute-id)
    ; If the action is commutative, check whether applying it results in a seen action combination:
    (let [new-commutation (update commutation type
                                  #(gcomm/add-commute-id type %1 %2)
                                  commute-id)
          [old new] (swap-vals! seen-commutations update type
                                #(gcomm/add-commutation type %1 %2)
                                new-commutation)]
      ; Only return the given state, if a new commutation is reached via the given action:
      (when (not= old new)
        (assoc! state
                :commutation new-commutation
                :tie-breaker (gcomm/tie-breaker type commute-id))))
    ; Non commutative actions result in completely new starting conditions.
    ; Thus all commutative action combinations can be tried again.
    ; The set of seen commutations is reset:
    (assoc! state
            :seen-commutations (atom {})
            :commutation {})))

(defn add-source-candidates!
  "Updates the set of potential value sources for all parameter flaws in the current state.
   Takes a transient state and an action.
   To reduce search cost, the candidate set is based on the previous candidates.
   It is updated by considering which CFG nodes are added or removed by the given action."
  [{:keys [db flaws] :as state} {add :add, remove-ids :remove}]
  (let [cands (if add
                ; If a new value source was potentially added, all flaws are rechecked for candidate sources:
                (into {}
                      (comp (filter #(gq/fillable-call-param? db %))
                            (map (fn [flaw] [flaw (gq/compatibly-typed-sources db flaw)])))
                      (:parameter flaws))
                (if (seq remove-ids)
                  ; If sources were only removed compared to the previous state, reduce the candidate map:
                  (let [remove-ids (set remove-ids)]
                    (into {}
                          (keep (fn [[k :as kv]]
                                  (when-not (contains? remove-ids k) kv)))
                          (:source-candidates state)))
                  ; If no sources were removed or added, keep the candidates unchanged:
                  (:source-candidates state)))]
    (assoc! state :source-candidates cands)))

(defn add-placeholder-matches!
  "Updates the set of potential placeholder matches.
   Takes a transient state and an action."
  [{:keys [db flaws placeholder-matches] :or {placeholder-matches {}} :as state} action]
  ; Only update the placeholder matches, if new information was added to the CFG:
  (if (:add action)
    (assoc! state :placeholder-matches
            (into placeholder-matches
                  (comp (remove placeholder-matches)
                        (map (fn [call]
                              (let [matches (gq/placeholder-matches db (gq/callable db call))
                                    heuristic (gc/match-completion-heuristic db matches)]
                                [call (with-meta matches {:heuristic heuristic})]))))
                  (:call flaws)))
    state))

(defn add-compatibly-typed-callables!
  "Updates the set of callabes that return values that could potentially fit into parameter flaws.
   Takes a transient state and an action.
   Only searches for potential callables for parameter flaws whose datatype was changed compared to the previous state.
   Previous search results for unchanged flaws are reused to reduce computational costs."
  [{:keys [db flaws compatibly-typed-callables] :or {compatibly-typed-callables {}} :as state}
   {:keys [type-changes]}]
  (if type-changes
    (let [xform (comp (filter #(gq/fillable-call-param? db %))
                      (map (fn [flaw] [flaw (delay (gq/compatibly-typed-callables db flaw))])))
          xform (if (= type-changes :all)
                  xform
                  (comp (filter #(or (contains? type-changes %)
                                     (not (contains? compatibly-typed-callables %))))
                        xform))]
      (assoc! state :compatibly-typed-callables
              (into compatibly-typed-callables xform
                    (:parameter flaws))))
    state))

(defn add-heuristic!
  "Takes a transient state and adds to it the total expected cost to reach a goal state from the current one."
  [state]
  (let [h (gc/cost-heuristic state)]
    (when (< h Double/POSITIVE_INFINITY)
      (assoc! state :heuristic (+ (:cost state) h)))))

(defn apply-action
  "Applies the given action to the given state.
   Returns the resulting state if this new state has not been tried before.
   If the resulting state has already been tried, `nil` is returned instead."
  [state action]
  (when (< (:cost action) Double/POSITIVE_INFINITY)
    (when-let [new-state (update-commutations! (transient state) action)]
      (as-> new-state $
            (assoc! $ :id (swap! *sid inc))
            (assoc! $ :predecessor state)
            (assoc! $ :last-action action)
            (update! $ :db d/db-with (:tx action))
            (update! $ :cost + (:cost action))
            (assoc! $ :flaws (flaws $))
            (add-source-candidates! $ action)
            (add-placeholder-matches! $ action)
            (add-compatibly-typed-callables! $ action)
            (add-heuristic! $)
            (persistent! $)))))

(defn initial-state
  "Takes a library scrape and a datascript transaction describing an incomplete CFG representing the starting conditions of a search.
   Returns a state representing those initial starting conditions."
  [scrape tx]
  (apply-action {:cost 0
                 :db (:db scrape)}
                {:cost 0, :tx tx, :add true, :type-changes :all}))
(defn actions
  "Takes a state and returns a collection of candidate actions that can be applied to that state."
  [state]
  (let [{p-flaws :parameter, c-flaws :call} (:flaws state)]
    (if (and (empty? p-flaws) (empty? c-flaws))
      :done
      (let [actions (transient [])
            actions (into! actions
                           (mapcat #(receive-actions state %))
                           p-flaws)
            merged-actions
            (update-into {}
                         (comp (mapcat #(concat (call-actions state %)
                                                (snippet-actions state %)))
                               (map (fn [a] [(:id a) a])))
                         (fn [a b]
                           (if (nil? a)
                             b
                             (update a :weight + (:weight b))))
                         p-flaws)
            actions (into! actions (vals merged-actions))
            actions (into! actions (mapcat #(param-remove-actions state %)) p-flaws)
            actions (into! actions (mapcat #(call-completion-actions state %)) c-flaws)
            actions (persistent! actions)]
        (gc/costify-actions actions)))))

(defn successors
  "Takes a state and returns a collection of successor states that can be reached from the given state."
  [state]
  (let [acts (actions state)]
    (if (= acts :done)
      :done
      (keep #(apply-action state %) acts))))

(defn search-state
  "Takes a state representing some specific candidate CFG.
   Returns a so called search state which represents the initial overall state of an A* search, which starts its search at the given state.
   Search state essentially represent a priority queue of states that still need to be considered by the search."
  [state]
  {:queue (priority-map state (:heuristic state))})

(def initial-search-state (comp search-state initial-state))

(defn continue-search
  "Takes an A* search state and returns the following A* search state.
   This function essentially advances a given A* search by one iteration."
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

(defn search
  "Takes an initial A* search state and advances the search by at most `limit` iterations.
   If a goal state was reached within those iterations, it is returned.
   Returns `nil` otherwise."
  [search-state limit]
  (let [succs (iterate continue-search search-state)
        succs (take limit succs)
        goal (some :goal succs)]
    goal))
