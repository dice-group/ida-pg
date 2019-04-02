(ns librarian.model.concepts.datatype
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]))

(defconcept datatype
  :attributes {::extends {:db/valueType :db.type/ref
                          :db/cardinality :db.cardinality/many
                          :db/doc "Supertype of the type."}}
  :spec ::datatype)

(s/def ::datatype (hs/entity-keys :opt [::extends]))
(s/def ::extends (s/coll-of (hs/instance? ::datatype)))
