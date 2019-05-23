(ns librarian.model.concepts.call-value
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.helpers.predicate :refer [p-or]]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.typed :refer [typed]]
            [librarian.model.concepts.data-receiver :refer [data-receiver]]))

(defconcept call-value [typed data-receiver]
  :attributes {::value {:db/doc "The value of this call-value box."}}
  :spec ::call-value)

(s/def ::call-value (hs/entity-keys :req [::value]))
(s/def ::value (p-or string? number? boolean?))
