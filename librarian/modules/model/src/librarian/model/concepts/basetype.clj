(ns librarian.model.concepts.basetype
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.helpers.transaction :refer [add-attr]]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.named :as named :refer [named]]
            [librarian.model.concepts.datatype :as datatype :refer [datatype]]))

(defconcept basetype [named datatype]
  :attributes {::id {:db/unique :db.unique/identity
                     :librarian/computed true
                     :db/doc "Unique name of the basetype."}}
  :spec ::basetype
  :preprocess {::named/name (add-attr ::id)})

(s/def ::basetype (hs/entity-keys :req [::id]))
