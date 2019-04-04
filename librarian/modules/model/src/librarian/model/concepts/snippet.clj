(ns librarian.model.concepts.snippet
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]))

(defconcept snippet
  :attributes {::entry-call {:db/valueType :db.type/ref
                             :db/cardinality :db.cardinality/many
                             :db/isComponent true
                             :db/doc "An entry call of this snippet."}}
  :spec ::snippet)

(s/def ::snippet (hs/entity-keys :opt [::entry-call]))
(s/def ::entry-call (hs/instance? :librarian.model.concepts.call/call))
