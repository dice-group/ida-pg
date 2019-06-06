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
             "c:" (:cost s) "h:" (:heuristic s) "flaws:" p-flaws c-flaws)
    search-state))

(defn gen-test*
  ([ds init]
   (gen-test* ds init 10))
  ([ds init limit]
   (let [scrape (scrape/read-scrape ds)
         search-state (gen/initial-search-state scrape init)
         i (atom 0)
         _ (print-search-state i search-state)
         succs (iterate (comp (partial print-search-state i)
                              gen/continue-search)
                        search-state)
         succs (take limit succs)]
     (time (doall succs))
     (time (rt/show-search-state (or (some #(when (:goal %) %) succs)
                                     (last succs))
                                 :show-patterns false)))))

(defn- goal-init-tx
  [inputs goals]
  (instances->tx (concat (mapv (fn [input]
                                 (instanciate call-result/call-result
                                   :datatype [(instanciate role-type/role-type
                                                :id input)]))
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
                           :value 123
                           :datatype [(instanciate basetype/basetype
                                        :name "string")
                                      (instanciate semantic-type/semantic-type
                                        :key "foo"
                                        :value "bar")
                                      (instanciate role-type/role-type
                                        :id :result)])
                         (instanciate call-parameter/call-parameter
                           :datatype [(instanciate basetype/basetype
                                        :name "int")])])
         args))

(defmethod gen-test :goal
  [_ & args]
  (apply gen-test*
         "libs/scikit-learn-cluster"
         (concat (goal-init-tx [:dataset] [:labels])
                 (instances->tx [(instanciate constant/constant
                                   :value 123
                                   :datatype [(instanciate basetype/basetype
                                                :name "string")])]))
         args))
