(ns lib-scraper.model.concepts.parameter
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.concepts.function :as function]))

(def concept {::name {:db/doc "Name of the parameter."}
              ::position {:db/doc "Position of the parameter."}
              ::optional {:db/doc "Denotes whether this parameter is optional."}})

(s/def ::name string?)
(s/def ::position (s/and int? #(<= 0 %)))
(s/def ::optional boolean?)
(s/def ::concept (hs/entity-keys :req [::name ::position
                                       ::function/_param]
                                 :opt [::optional]))
