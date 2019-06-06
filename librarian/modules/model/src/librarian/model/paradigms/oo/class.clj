(ns librarian.model.paradigms.oo.class
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.namespaced :as namespaced :refer [namespaced]]
            [librarian.model.concepts.datatype :as datatype :refer [datatype]]
            [librarian.model.concepts.typed :refer [typed]])
  (:refer-clojure :exclude [class]))

(defconcept class [typed namespaced datatype]
  :attributes {::constructor {:db/valueType :db.type/ref
                              :db/cardinality :db.cardinality/many
                              :db/isComponent true
                              :db/index true
                              :db/doc "Constructor of the class."}
               ::method {:db/valueType :db.type/ref
                         :db/cardinality :db.cardinality/many
                         :db/isComponent true
                         :db/index true
                         :db/doc "Method of the class."}}
  :spec ::class)

(s/def ::class (hs/entity-keys :req [::constructor]
                               :opt [::method]))
(s/def ::constructor (s/coll-of (hs/instance? :librarian.model.paradigms.oo.constructor/constructor)))
(s/def ::method (s/coll-of (hs/instance? :librarian.model.paradigms.oo.method/method)))
