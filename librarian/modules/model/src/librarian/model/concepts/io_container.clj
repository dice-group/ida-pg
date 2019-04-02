(ns librarian.model.concepts.io-container
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.named :refer [named]]
            [librarian.model.concepts.typed :refer [typed]]))

(defconcept io-container [named typed]
  :attributes {::position {:db/doc "Position of the io-container. Only required if there are multiple io-containers."}}
  :spec ::io-container)

(s/def ::io-container (hs/entity-keys :opt [::position]))
(s/def ::position (s/and int? #(<= 0 %)))
