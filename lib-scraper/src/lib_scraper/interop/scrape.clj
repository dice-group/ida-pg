(ns lib-scraper.interop.scrape
  (:require [lib-scraper.io.core :as io]
            [lib-scraper.io.scrape :as scrape]
            [clojure.edn :as edn])
  (:gen-class :name lib_scraper.interop.Scrape
              :prefix "scrape-"
              :state scrape
              :init init
              :factory load
              :main false
              :constructors {[String] []
                             [java.io.File] []}
              :methods [[query [String] java.util.List]
                        [query [String java.lang.Iterable] java.util.List]
                        [querySingle [String] Object]
                        [querySingle [String java.lang.Iterable] Object]
                        [getName [] String]
                        [getCreationDate [] java.util.Date]
                        [getEcosystemName [] String]
                        [getEcosystemVersion [] String]]))

(defn scrape-init
  [file]
  [[] (scrape/read-scrape file)])

(defn scrape-querySingle
  ([this, ^String query]
   (scrape-querySingle this query []))
  ([this, ^String query, ^java.lang.Iterable inputs]
   (apply io/query-scrape (.scrape this)
          (edn/read-string query) inputs)))

(defn scrape-query
  ([this, ^String query]
   (vec (scrape-querySingle this query)))
  ([this, ^String query, ^java.lang.Iterable inputs]
   (vec (scrape-querySingle this query inputs))))

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
