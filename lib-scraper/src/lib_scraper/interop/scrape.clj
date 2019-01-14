(ns lib-scraper.interop.scrape
  (:require [lib-scraper.io.core :as io]
            [lib-scraper.io.scrape :as scrape]
            [clojure.edn :as edn])
  (:import (java.util ArrayList))
  (:gen-class :name lib_scraper.Scrape
              :prefix "scrape-"
              :init init
              :factory load
              :state scrape
              :constructors {[String] []
                             [java.io.File] []}
              :methods [[query [String] java.util.ArrayList]
                        [query [String java.lang.Iterable] java.util.ArrayList]
                        [getName [] String]
                        [getCreationDate [] java.util.Date]
                        [getEcosystemName [] String]
                        [getEcosystemVersion [] String]]))

(defn scrape-init
  [file]
  [[] (scrape/read-scrape file)])

(defn scrape-query
  ([this, ^String query]
   (scrape-query this query []))
  ([this, ^String query, inputs]
   (let [result (apply io/query-scrape (.scrape this)
                       (edn/read-string query) inputs)]
     (ArrayList. (map (partial into-array Object)
                      result)))))

(defn scrape-getName
  [this]
  (str (:name (.scrape this))))

(defn scrape-getCreationDate
  [this]
  (:created (.scrape this)))

(defn scrape-getEcosystemName
  [this]
  (str (:alias (:ecosystem (.scrape this)))))

(defn scrape-getEcosystemVersion
  [this]
  (str (:ecosystem/version (.scrape this))))
