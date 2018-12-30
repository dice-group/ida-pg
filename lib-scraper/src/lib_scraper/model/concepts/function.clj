(ns lib-scraper.model.concepts.function
  (:require [clojure.spec.alpha :as s]))

(def concept {::name {:db/type :db.type/string
                      :db/doc "Name of the function."}
              ::param {:db/type :db.type/ref
                       :db/cardinality :db.cardinality/many
                       :db/doc "A parameter of the function."}})

(s/def ::name string?)
(s/def ::concept (s/keys :req [::name]))
