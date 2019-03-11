(ns librarian.model.concepts.callable
  (:require [librarian.model.syntax :refer [defconcept]]))

(defconcept callable
  :attributes {::parameter {:db/valueType :db.type/ref
                            :db/cardinality :db.cardinality/many
                            :db/isComponent true
                            :db/doc "A parameter of the callable."}
               ::result {:db/valueType :db.type/ref
                         :db/cardinality :db.cardinality/many
                         :db/isComponent true
                         :db/doc "A result (i.e. return value) of the callable."}})
