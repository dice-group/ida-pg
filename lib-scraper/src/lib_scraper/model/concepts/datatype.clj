(ns lib-scraper.model.concepts.datatype
  (:require [lib-scraper.helpers.transaction :refer [add-attr]]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.model.concepts.named :as named :refer [named]]))

(defconcept datatype [named]
  :attributes {::id {:db/unique :db.unique/identity
                     :db/doc "Unique name of the datatype."}
               ::instance {:db/valueType :db.type/ref
                           :db/cardinality :db.cardinality/many
                           :db/doc "Concepts of this type."}}
  :preprocess {::named/name (add-attr ::id)})