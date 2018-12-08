(ns lib-scraper.model.concepts.datatype)

(def spec {::name {:db/type :db.type/string
                   :db/unique :db.unique/identity
                   :db/doc "Unique name of the datatype."}
           ::instance {:db/type :db.type/ref
                       :db/cardinality :db.cardinality/many
                       :db/doc "Concepts of this type."}})
