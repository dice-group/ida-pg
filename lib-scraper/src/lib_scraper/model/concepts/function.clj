(ns lib-scraper.model.concepts.function)

(def spec {::name {:db/type :db.type/string
                   :db/doc "Name of the function."}
           ::description {:db/type :db.type/string
                          :db/doc "Docstring for the function."}
           ::param {:db/type :db.type/ref
                    :db/cardinality :db.cardinality/many
                    :db/doc "A parameter of the function."}
           ::returns {:db/type :db.type/ref
                      :db/doc "Return type of the function."}})
