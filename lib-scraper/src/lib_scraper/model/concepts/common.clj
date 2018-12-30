(ns lib-scraper.model.concepts.common)

(def concept {:description {:db/type :db.type/string
                            :db/cardinality :db.cardinality/many
                            :db/doc "Docstring for the concept."}
              :type {:db/type :db.type/keyword
                     :db/doc "Type of the concept."}
              :source {:db/type :db.type/string
                       :db/doc "The datasource this entity originates from. Typically a URL."}
              :datatype {:db/type :db.type/ref
                         :db/doc "Datatype of the concept if applicable."}})
