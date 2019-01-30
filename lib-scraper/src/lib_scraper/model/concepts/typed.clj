(ns lib-scraper.model.concepts.typed
  (:require [lib-scraper.model.syntax :refer [defconcept]]))

(defconcept typed
  :attributes {::datatype {:db/valueType :db.type/ref
                           :db/doc "Datatype of this concept."}})
