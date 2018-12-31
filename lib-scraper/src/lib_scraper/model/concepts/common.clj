(ns lib-scraper.model.concepts.common)

(def concept {:description {:db/type :db.type/string
                            :db/cardinality :db.cardinality/many
                            :db/doc "Docstring for the concept."}
              :datatype {:db/type :db.type/ref
                         :db/doc "Datatype of the concept if applicable."}})
