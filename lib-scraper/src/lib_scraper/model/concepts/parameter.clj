(ns lib-scraper.model.concepts.parameter
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]))

(def concept {::name {:db/type :db.type/string
                      :db/doc "Name of the parameter."}
              ::position {:db/type :db.type/long
                          :db/doc "Position of the parameter."}
              ::optional {:db/type :db.type/boolean
                          :db/doc "Denotes whether this parameter is optional."}})

(s/def ::name string?)
(s/def ::position int?)
(s/def ::optional boolean?)
(s/def ::concept (hs/entity-keys :req [::name ::position]
                                 :opt [::optional]))
