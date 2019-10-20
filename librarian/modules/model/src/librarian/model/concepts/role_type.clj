(ns librarian.model.concepts.role-type
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.datatype :as datatype :refer [datatype]]))

(defconcept role-type [datatype]
  :attributes {::id {:db/unique :db.unique/identity
                     :db/doc "Identifier of the role-type."}}
  :spec ::role-type)

(s/def ::role-type (hs/entity-keys :req [::id]))
(s/def ::id keyword?)
