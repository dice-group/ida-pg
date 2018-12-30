(ns lib-scraper.model.concepts.class
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.model.concepts.package :as package]))

(def concept {::id {:db/type :db.type/string
                    :db/unique :db.unique/identity}
              ::name {:db/type :db.type/string
                      :db/doc "Name of the class."}
              ::constructor {:db/type :db.type/ref
                             :db/cardinality :db.cardinality/many
                             :db/doc "Constructor of the class. A ref to a function."}
              ::method {:db/type :db.type/ref
                        :db/cardinality :db.cardinality/many
                        :db/doc "Method of the class. A ref to a function."}})

(s/def ::name string?)
(s/def ::concept (s/keys :req [::name]
                         :opt [::package/_member]))

(s/valid? ::concept {::name "test"})
(s/keys :req [::name]
        :opt [::package/_member])
