(ns lib-scraper.io.scrape
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [datascript.core :as d])
  (:import (java.util.zip GZIPInputStream
                          GZIPOutputStream)))

(defn read-scrape
  [file]
  (with-open [data (-> file
                       (io/input-stream)
                       (GZIPInputStream.))]
    (edn/read-string {:readers d/data-readers}
                     (slurp data))))

(defn write-scrape
  [file {:keys [name ecosystem meta]} scrape]
  (io/make-parents file)
  (let [out {:scrape scrape
             :name name
             :created (java.util.Date.)
             :ecosystem (:alias ecosystem)
             :ecosystem/version (:version ecosystem)
             :meta meta}]
    (with-open [data (-> file
                         (io/output-stream)
                         (GZIPOutputStream.))]
      (io/copy (pr-str out) data))))
