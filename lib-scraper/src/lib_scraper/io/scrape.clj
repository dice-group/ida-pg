(ns lib-scraper.io.scrape
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [me.raynes.fs :as fs]
            [clojure.edn :as edn]
            [datascript.core :as d])
  (:import (java.util.zip GZIPInputStream
                          GZIPOutputStream)))

(def default-name "scrape.db")

(defn- get-file
  [file]
  (cond
    (fs/directory? file) (fs/file file default-name)
    (fs/file? file) (fs/file file)
    :else (throw (Error. "Invalid scrape path."))))

(defn read-scrape
  [file]
  (let [file (get-file file)]
    (with-open [data (-> file
                         (io/input-stream)
                         (GZIPInputStream.))]
      (edn/read-string {:readers d/data-readers}
                       (slurp data)))))

(defn write-scrape
  [file {:keys [name ecosystem meta]} scrape]
  (let [file (get-file file)
        out {:scrape scrape
             :name name
             :created (java.util.Date.)
             :ecosystem (:alias ecosystem)
             :ecosystem/version (:version ecosystem)
             :meta meta}]
    (log/info (str "Writing scrape to " file "..."))
    (io/make-parents file)
    (with-open [data (-> file
                         (io/output-stream)
                         (GZIPOutputStream.))]
      (io/copy (pr-str out) data))
    (log/info (str "Successfully wrote scrape to " file "."))))
