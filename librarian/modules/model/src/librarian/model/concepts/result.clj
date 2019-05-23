(ns librarian.model.concepts.result
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.io-container :refer [io-container]]
            [librarian.model.concepts.data-receiver :refer [data-receiver]]
            [librarian.model.concepts.callable :as callable]))

(defconcept result [io-container data-receiver]
  :spec ::result)

(s/def ::result (hs/entity-keys :req [::callable/_result]))
