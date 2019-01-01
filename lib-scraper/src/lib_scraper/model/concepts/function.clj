(ns lib-scraper.model.concepts.function
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.concepts.package :as package]
            [lib-scraper.model.concepts.class :as class]))

(def concept {::name {:db/type :db.type/string
                      :db/doc "Name of the function."}
              ::param {:db/type :db.type/ref
                       :db/cardinality :db.cardinality/many
                       :db/doc "A parameter of the function."}})

(s/def ::name string?)
(s/def ::concept (hs/entity-keys :req [::name
                                       (or ::class/_method
                                           ::class/_constructor
                                           ::package/_member)]))