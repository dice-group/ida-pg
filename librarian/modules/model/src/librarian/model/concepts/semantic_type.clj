(ns librarian.model.concepts.semantic-type
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.datatype :as datatype :refer [datatype]]))

(defconcept semantic-type [datatype]
  :attributes {::key {:db/doc "Context string of the semantic type."}
               ::value {:db/doc "Description of the semantics of this type."}}
  :spec ::semantic-type)

(s/def ::semantic-type (hs/entity-keys :req [::value] :opt [::key]))
(s/def ::key string?)
(s/def ::value string?)
