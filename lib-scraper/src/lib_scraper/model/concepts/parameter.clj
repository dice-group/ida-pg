(ns lib-scraper.model.concepts.parameter
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.model.concepts.named :refer [named]]
            [lib-scraper.model.concepts.callable :as callable]))

(defconcept parameter [named]
  :attributes {::position {:db/doc "Position of the parameter."}
               ::optional {:db/doc "Denotes whether this parameter is optional."}}
  :spec ::parameter)

(s/def ::parameter (hs/entity-keys :req [::position ::callable/_parameter]
                                   :opt [::optional]))
(s/def ::position (s/and int? #(<= 0 %)))
(s/def ::optional boolean?)
