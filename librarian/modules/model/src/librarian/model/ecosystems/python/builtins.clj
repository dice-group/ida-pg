(ns librarian.model.ecosystems.python.builtins
  (:require [librarian.model.syntax :refer [instanciate instances->tx]]
            [librarian.model.concepts.basetype :as basetype]
            [librarian.model.concepts.namespace :as namespace]
            [librarian.model.paradigms.functional.function :as function]))

(def basetypes (into {} (map (fn [type]
                               [type (instanciate basetype/basetype
                                       :name (name type))])
                             [:int :float :complex :str])))

(def global-ns (instanciate namespace/namespace
                 :name ""))

(defn- instanciate-typecaster
  [name from to]
  (instanciate function/function
    :name name
    ::namespace/_member global-ns
    :parameter []))

(def typecasters [(instanciate-typecaster "int" :str :int)
                  (instanciate-typecaster "float" :str :float)])

(def builtins (concat (vals basetypes)
                      typecasters))
