(ns lib-scraper.model.concepts.package
  (:require [lib-scraper.helpers.transaction :refer [add-attr]]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.model.concepts.named :as named :refer [named]]))

(defconcept package [named]
  :attributes {::id {:db/unique :db.unique/identity
                     :db/doc "Unique name of the package."}
               ::member {:db/valueType :db.type/ref
                         :db/cardinality :db.cardinality/many
                         :db/isComponent true
                         :db/doc "Concept is member of the package."}}
  :postprocess (add-attr ::id ::named/name))
