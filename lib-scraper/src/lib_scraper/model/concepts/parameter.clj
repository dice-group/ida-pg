(ns lib-scraper.model.concepts.parameter)

(def spec {::name {:db/type :db.type/string
                   :db/doc "Name of the parameter."}
           ::description {:db/type :db.type/string
                          :db/doc "Docstring for the parameter."}
           ::position {:db/type :db.type/long
                       :db/doc "Position of the parameter."}
           ::type {:db/type :db.type/ref
                   :db/doc "Type of the parameter."}})
