(ns lib-scraper.model.concepts.package
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]))

(def concept {::name {:db/unique :db.unique/identity
                      :db/doc "Unique name of the package."}
              ::member {:db/valueType :db.type/ref
                        :db/cardinality :db.cardinality/many
                        :db/isComponent true
                        :db/doc "Concept is member of the package."}})

(s/def ::name string?)
(s/def ::concept (hs/entity-keys :req [::name]))
