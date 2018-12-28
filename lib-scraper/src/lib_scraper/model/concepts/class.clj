(ns lib-scraper.model.concepts.class)

(def spec {::name {:db/type :db.type/string
                   :db/doc "Name of the class."}
           ::constructor {:db/type :db.type/ref
                          :db/cardinality :db.cardinality/many
                          :db/doc "Constructor of the class. A ref to a function."}
           ::method {:db/type :db.type/ref
                     :db/cardinality :db.cardinality/many
                     :db/doc "Method of the class. A ref to a function."}})
