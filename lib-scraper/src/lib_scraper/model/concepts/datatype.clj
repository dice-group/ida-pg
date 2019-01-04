(ns lib-scraper.model.concepts.datatype
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.helpers.spec :as hs]))

(defconcept datatype
  :attributes {::name {:db/unique :db.unique/identity
                       :db/doc "Unique name of the datatype."}
               ::instance {:db/valueType :db.type/ref
                           :db/cardinality :db.cardinality/many
                           :db/doc "Concepts of this type."}}
  :spec ::datatype)

(s/def ::datatype (hs/entity-keys :req [::name]))
(s/def ::name string?)
