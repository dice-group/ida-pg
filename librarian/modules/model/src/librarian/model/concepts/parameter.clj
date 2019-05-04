(ns librarian.model.concepts.parameter
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.io-container :refer [io-container]]
            [librarian.model.concepts.positionable :as positionable]
            [librarian.model.concepts.callable :as callable]))

(defconcept parameter [io-container]
  :attributes {::optional {:db/doc "Denotes whether this parameter is optional."}}
  :spec ::parameter)

(s/def ::parameter (hs/entity-keys :req [::positionable/position ::callable/_parameter]
                                   :opt [::optional]))
(s/def ::optional boolean?)
