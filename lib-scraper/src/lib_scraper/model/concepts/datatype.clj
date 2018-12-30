(ns lib-scraper.model.concepts.datatype
  (:require [clojure.spec.alpha :as s]))

(def concept {::name {:db/type :db.type/string
                      :db/unique :db.unique/identity
                      :db/doc "Unique name of the datatype."}
              ::instance {:db/type :db.type/ref
                          :db/cardinality :db.cardinality/many
                          :db/doc "Concepts of this type."}})

(s/def ::name string?)
(s/def ::concept (s/keys :req [::name]))
