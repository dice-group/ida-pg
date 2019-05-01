(ns librarian.model.concepts.snippet
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]))

(defconcept snippet
  :attributes {::contains {:db/valueType :db.type/ref
                           :db/cardinality :db.cardinality/many
                           :db/isComponent true
                           :db/index true
                           :db/doc "A control-flow concept that is part of this snippet."}}
  :spec ::snippet)

(s/def ::snippet (hs/entity-keys :opt [::contains]))
