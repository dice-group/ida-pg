(ns librarian.model.concepts.constant
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.helpers.transaction :refer [add-attr]]
            [librarian.helpers.predicate :refer [p-or]]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.typed :as typed :refer [typed]]
            [librarian.model.concepts.data-receiver :refer [data-receiver]]
            [librarian.model.concepts.datatype :refer [datatype]]))

(defconcept constant [typed datatype data-receiver]
  :attributes {::value {:db/doc "The value of this constant."}}
  :spec ::constant
  :preprocess {:db/id (add-attr ::typed/datatype)})

(s/def ::constant (hs/entity-keys :req [::value]))
(s/def ::value (p-or string? number? boolean?))
