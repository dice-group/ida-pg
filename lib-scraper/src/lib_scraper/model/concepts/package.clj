(ns lib-scraper.model.concepts.package
  (:require [datascript.core :as d]
            [datascript.db :as db]))

(def spec {::name {:db/type :db.type/string
                   :db/unique :db.unique/identity
                   :db/doc "Unique name of the package."}})
