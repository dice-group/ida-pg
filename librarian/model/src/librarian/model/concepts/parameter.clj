(ns librarian.model.concepts.parameter
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.named :refer [named]]
            [librarian.model.concepts.typed :refer [typed]]
            [librarian.model.concepts.callable :as callable]))

(defconcept parameter [named typed]
  :attributes {::position {:db/doc "Position of the parameter."}
               ::optional {:db/doc "Denotes whether this parameter is optional."}}
  :spec ::parameter)

(s/def ::parameter (hs/entity-keys :req [::position ::callable/_parameter]
                                   :opt [::optional]))
(s/def ::position (s/and int? #(<= 0 %)))
(s/def ::optional boolean?)
