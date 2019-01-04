(ns lib-scraper.model.concepts.parameter
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.concepts.function :as function]))

(defconcept parameter
  :attributes {::name {:db/doc "Name of the parameter."}
               ::position {:db/doc "Position of the parameter."}
               ::optional {:db/doc "Denotes whether this parameter is optional."}}
  :spec ::parameter)

(s/def ::parameter (hs/entity-keys :req [::name ::position
                                         ::function/_parameter]
                                   :opt [::optional]))
(s/def ::name string?)
(s/def ::position (s/and int? #(<= 0 %)))
(s/def ::optional boolean?)
