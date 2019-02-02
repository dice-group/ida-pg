(ns librarian.model.concepts.named
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]))

(defconcept named
  :attributes {::name {:db/index true
                       :db/doc "Name of the concept."}}
  :spec ::named)

(s/def ::named (hs/entity-keys :req [::name]))
(s/def ::name string?)
