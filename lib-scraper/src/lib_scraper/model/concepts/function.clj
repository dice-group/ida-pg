(ns lib-scraper.model.concepts.function
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.helpers.spec :as hs]))

(defconcept function
  :attributes {::name {:db/doc "Name of the function."}
               ::parameter {:db/valueType :db.type/ref
                            :db/cardinality :db.cardinality/many
                            :db/isComponent true
                            :db/doc "A parameter of the function."}}
  :spec ::function)

(s/def ::function (hs/entity-keys :req [::name]))
(s/def ::name string?)
