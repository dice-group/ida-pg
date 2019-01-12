(ns lib-scraper.io.scrape
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [datascript.core :as d]))

(defn read-scrape
  [path]
  (edn/read-string {:readers d/data-readers}
                   (slurp path)))

(defn write-scrape
  [path {:keys [name ecosystem meta]} scrape]
  (io/make-parents path)
  (let [out {:scrape scrape
             :name name
             :created (java.util.Date.)
             :ecosystem (:alias ecosystem)
             :ecosystem/version (:version ecosystem)
             :meta meta}]
    (spit path (pr-str out))))
