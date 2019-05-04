(ns librarian.model.concepts.positionable
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]))

(defconcept positionable
  :attributes {::position {:db/doc "Ordinal position of this concept."}}
  :spec ::positionable)

(s/def ::positionable (hs/entity-keys :opt [::position]))
(s/def ::position int?)
