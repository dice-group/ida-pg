(ns librarian.model.concepts.goal-type
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.datatype :as datatype :refer [datatype]]))

(defconcept goal-type [datatype]
  :attributes {::id {:db/unique :db.unique/identity
                     :db/doc "Identifier of the goal-type."}}
  :spec ::goal-type)

(s/def ::goal-type (hs/entity-keys :req [::id]))
(s/def ::id keyword?)
