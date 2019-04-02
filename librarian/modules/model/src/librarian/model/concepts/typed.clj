(ns librarian.model.concepts.typed
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]))

(defconcept typed
  :attributes {::datatype {:db/valueType :db.type/ref
                           :db/cardinality :db.cardinality/many
                           :db/doc "Datatype of this concept."}}
  :spec ::typed)

(s/def ::typed (hs/entity-keys :opt [::datatype]))
(s/def ::datatype (s/coll-of (hs/instance? :librarian.model.concepts.datatype/datatype)))
