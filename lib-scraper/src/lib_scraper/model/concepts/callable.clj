(ns lib-scraper.model.concepts.callable
  (:require [lib-scraper.model.syntax :refer [defconcept]]))

(defconcept callable
  :attributes {::parameter {:db/valueType :db.type/ref
                            :db/cardinality :db.cardinality/many
                            :db/isComponent true
                            :db/doc "A parameter of the callable."}})
