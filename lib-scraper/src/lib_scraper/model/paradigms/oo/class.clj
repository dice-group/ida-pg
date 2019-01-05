(ns lib-scraper.model.paradigms.oo.class
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.helpers.transaction :refer [add-attr]]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.model.concepts.named :as named :refer [named]]
            [lib-scraper.model.concepts.package :as package])
  (:refer-clojure :exclude [class]))

(defn fqn
  "Computes the fully qualified name of a given class entity."
  [e]
  (when-let [pname (some-> e ::package/_member ::named/name)]
    (str pname "." (::named/name e))))

(defconcept class [named]
  :attributes {::id {:db/unique :db.unique/identity
                     :db/doc "Fully qualified name of the class."}
                ::constructor {:db/valueType :db.type/ref
                               :db/cardinality :db.cardinality/many
                               :db/isComponent true
                               :db/doc "Constructor of the class. A ref to a function."}
                ::method {:db/valueType :db.type/ref
                          :db/cardinality :db.cardinality/many
                          :db/isComponent true
                          :db/doc "Method of the class. A ref to a function."}}
  :spec ::class
  :postprocess (add-attr ::id fqn))

(s/def ::class (hs/entity-keys :req [::id ::constructor ::package/_member]))
