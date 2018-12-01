(ns lib-scraper.model.concepts.package)

(def spec {::name {:db/type :db.type/string
                   :db/unique :db.unique/identity
                   :db/doc "Unique name of the package."}})
