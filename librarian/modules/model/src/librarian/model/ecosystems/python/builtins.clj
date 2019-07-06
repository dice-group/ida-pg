(ns librarian.model.ecosystems.python.builtins
  (:require [librarian.model.syntax :refer [instanciate instances->tx]]
            [librarian.model.concepts.basetype :as basetype]
            [librarian.model.concepts.namespace :as namespace]
            [librarian.model.paradigms.functional.function :as function]
            [librarian.model.concepts.parameter :as parameter]
            [librarian.model.concepts.result :as result]))

(def object-type (instanciate basetype/basetype :name "object"))

(def basetypes (into {:object object-type}
                     (map (fn [type]
                            [type (instanciate basetype/basetype
                                    :name (name type)
                                    :extends object-type)]))
                     [:int :float :complex :string :boolean]))

(def global-ns (instanciate namespace/namespace :name ""))

(defn- instanciate-typecaster
  [name from to]
  (let [param (instanciate parameter/parameter
                :position 0
                :name "x"
                :datatype (basetypes from))]
    (instanciate function/function
      :name name
      ::namespace/_member global-ns
      :parameter param
      :result (instanciate result/result
                :position 0
                :name "y"
                :datatype (basetypes to)
                :receives-semantic param))))

(def typecasters [(instanciate-typecaster "str" :object :string)
                  (instanciate-typecaster "int" :object :int)
                  (instanciate-typecaster "float" :object :float)])

(def builtins (concat (vals basetypes)
                      typecasters))
