(ns lib-scraper.model.concepts.package
  (:require [clojure.spec.alpha :as s]))

(def concept {::name {:db/type :db.type/string
                      :db/unique :db.unique/identity
                      :db/doc "Unique name of the package."}
              ::member {:db/type :db.type/ref
                        :db/cardinality :db.cardinality/many
                        :db/doc "Concept is member of the package."}})

(s/def ::name string?)
(s/def ::concept (s/keys :req [::name]))
