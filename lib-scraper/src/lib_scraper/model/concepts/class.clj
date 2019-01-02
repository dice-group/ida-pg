(ns lib-scraper.model.concepts.class
  (:require [clojure.spec.alpha :as s]
            [datascript.core :as d]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.concepts.package :as package]))

(def concept {::id {:db/unique :db.unique/identity
                    :db/doc "Fully qualified name of the class."}
              ::name {:db/doc "Name of the class."}
              ::constructor {:db/valueType :db.type/ref
                             :db/cardinality :db.cardinality/many
                             :db/isComponent true
                             :db/doc "Constructor of the class. A ref to a function."}
              ::method {:db/valueType :db.type/ref
                        :db/cardinality :db.cardinality/many
                        :db/isComponent true
                        :db/doc "Method of the class. A ref to a function."}})

(s/def ::name string?)
(s/def ::concept (hs/entity-keys :req [::id ::name ::package/_member]))

(defn postprocess
  [db id]
  (let [e (d/entity db id)]
      (when-let [pname (some-> e ::package/_member ::package/name)]
        [[:db/add id ::id (str pname "." (::name e))]])))
