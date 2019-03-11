(ns librarian.model.concepts.parameter
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.io-value :as io-value :refer [io-value]]
            [librarian.model.concepts.callable :as callable]))

(defconcept parameter [io-value]
  :attributes {::optional {:db/doc "Denotes whether this parameter is optional."}}
  :spec ::parameter)

(s/def ::parameter (hs/entity-keys :req [::io-value/position ::callable/_parameter]
                                   :opt [::optional]))
(s/def ::optional boolean?)
