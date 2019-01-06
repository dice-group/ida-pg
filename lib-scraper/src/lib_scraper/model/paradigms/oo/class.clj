(ns lib-scraper.model.paradigms.oo.class
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.model.concepts.namespaced :refer [namespaced]])
  (:refer-clojure :exclude [class]))

(defconcept class [namespaced]
  :attributes {::constructor {:db/valueType :db.type/ref
                              :db/cardinality :db.cardinality/many
                              :db/isComponent true
                              :db/doc "Constructor of the class. A ref to a function."}
                ::method {:db/valueType :db.type/ref
                          :db/cardinality :db.cardinality/many
                          :db/isComponent true
                          :db/doc "Method of the class. A ref to a function."}}
  :spec ::class)

(s/def ::class (hs/entity-keys :req [::constructor]))
