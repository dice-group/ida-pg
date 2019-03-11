(ns librarian.model.concepts.io-value
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.named :refer [named]]
            [librarian.model.concepts.typed :refer [typed]]))

(defconcept io-value [named typed]
  :attributes {::position {:db/doc "Position of the io-value. Only required if there are multiple io-values."}}
  :spec ::io-value)

(s/def ::io-value (hs/entity-keys :opt [::position]))
(s/def ::position (s/and int? #(<= 0 %)))
