(ns lib-scraper.model.concepts.package
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.helpers.spec :as hs]))

(defconcept package
  :attributes {::name {:db/unique :db.unique/identity
                       :db/doc "Unique name of the package."}
               ::member {:db/valueType :db.type/ref
                         :db/cardinality :db.cardinality/many
                         :db/isComponent true
                         :db/doc "Concept is member of the package."}}
  :spec ::package)

(s/def ::package (hs/entity-keys :req [::name]))
(s/def ::name string?)
