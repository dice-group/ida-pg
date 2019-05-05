(ns librarian.model.paradigms.oo.class
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.namespaced :refer [namespaced]])
  (:refer-clojure :exclude [class]))

(defconcept class [namespaced]
  :attributes {::constructor {:db/valueType :db.type/ref
                              :db/cardinality :db.cardinality/many
                              :db/isComponent true
                              :db/doc "Constructor of the class."}
               ::extends {:db/valueType :db.type/ref
                          :db/cardinality :db.cardinality/many
                          :db/doc "Superclass of the class."}
               ::method {:db/valueType :db.type/ref
                         :db/cardinality :db.cardinality/many
                         :db/isComponent true
                         :db/doc "Method of the class."}}
  :spec ::class)

(s/def ::class (hs/entity-keys :req [::constructor]))
