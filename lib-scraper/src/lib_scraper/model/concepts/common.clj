(ns lib-scraper.model.concepts.common)

(def spec {:description {:db/type :db.type/string
                         :db/doc "Docstring for the concept."}
           :type {:db/type :db.type/ref
                  :db/doc "Type of the concept."}})
