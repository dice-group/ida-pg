(ns generator
  (:require [librarian.generator.core :as gen]
            [librarian.model.io.scrape :as scrape]
            [librarian.model.syntax :refer [instanciate instances->tx]]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.constant :as constant]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.basetype :as basetype]
            [librarian.model.concepts.role-type :as role-type]
            [librarian.model.concepts.semantic-type :as semantic-type]
            [repl-tools :as rt]))

(defn- print-search-state
  [i search-state]
  (let [s (rt/search-state->next-state search-state)
        {p-flaws :parameter, c-flaws :call} (:flaws s)]
    (println (format "%6d " (swap! i inc))
             "id:" (:id s) "pred:" (:id (:predecessor s))
             "c:" (:cost s) "h:" (:heuristic s)
             "flaws:" p-flaws c-flaws
             (some-> s :last-action :type name))
    search-state))

(defn gen-test*
  ([ds init]
   (gen-test* ds init 50))
  ([ds init limit]
   (binding [gen/*sid (atom 0)]
     (let [scrape (scrape/read-scrape ds)
           search-state (gen/initial-search-state scrape init)
           i (atom 0)
           _ (print-search-state i search-state)
           succs (iterate (comp (partial print-search-state i)
                                gen/continue-search)
                          search-state)
           succs (take limit succs)
           res (time (some #(when (:goal %) %) succs))
           res (or res (last succs))
           state (vary-meta (rt/search-state->next-state res)
                            assoc :scrape scrape)]
       (rt/show-state state)))))

(defn- goal-init-tx
  [inputs goals]
  (instances->tx (concat (map-indexed (fn [idx input]
                                        (instanciate call-result/call-result
                                          :datatype [(instanciate role-type/role-type
                                                       :id input)]
                                          :position (inc idx)))
                                      inputs)
                         (mapv (fn [goal]
                                 (instanciate call-parameter/call-parameter
                                   :datatype [(instanciate role-type/role-type
                                                :id goal)]))
                               goals))))

(defmulti gen-test (fn [t & args] t))

(defmethod gen-test :base
  [_ & args]
  (apply gen-test*
         "libs/scikit-learn-cluster"
         (instances->tx [(instanciate constant/constant
                           :value "123"
                           :datatype [(instanciate basetype/basetype
                                        :name "string")
                                      (instanciate semantic-type/semantic-type
                                        :key "name"
                                        :value "foo")])
                         (instanciate constant/constant
                           :value 42
                           :datatype [(instanciate basetype/basetype
                                        :name "int")
                                      (instanciate semantic-type/semantic-type
                                        :key "name"
                                        :value "bar")])
                         (instanciate call-parameter/call-parameter
                           :datatype [(instanciate basetype/basetype
                                        :name "int")
                                      (instanciate semantic-type/semantic-type
                                        :key "name"
                                        :value "foo")])])
         args))

(defmethod gen-test :goal
  [_ & args]
  (apply gen-test*
         "libs/scikit-learn-cluster"
         (concat (goal-init-tx [:dataset] [:labels])
                 (instances->tx [(instanciate call-result/call-result
                                   :datatype [(instanciate basetype/basetype
                                                :name "string")
                                              (instanciate semantic-type/semantic-type
                                                :key "name"
                                                :value "n_clusters")]
                                   :position 0)
                                 (instanciate constant/constant
                                   :value 42
                                   :datatype [(instanciate basetype/basetype
                                                :name "int")
                                              (instanciate semantic-type/semantic-type
                                                :key "name"
                                                :value "abc")])
                                 (instanciate constant/constant
                                   :value 1/2
                                   :datatype [(instanciate basetype/basetype
                                                :name "float")
                                              (instanciate semantic-type/semantic-type
                                                :key "name"
                                                :value "tol")])
                                 #_(instanciate call-parameter/call-parameter
                                     :datatype [(instanciate basetype/basetype
                                                  :name "float")
                                                (instanciate semantic-type/semantic-type
                                                  :key "name"
                                                  :value "centroid")])]))
         args))
