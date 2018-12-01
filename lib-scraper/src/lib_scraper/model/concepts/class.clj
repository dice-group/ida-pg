(ns lib-scraper.model.concepts.class)

(def spec {::name {:db/type :db.type/string
                   :db/doc "Name of the class."}
           ::package {:db/type :db.type/ref
                      :db/doc "Package of the class."}
           ::method {:db/type :db.type/ref
                     :db/cardinality :db.cardinality/many
                     :db/doc "Method of the class. A ref to a function."}})
