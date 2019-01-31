(ns lib-scraper.io.core
  (:require [me.raynes.fs :as fs]
            [lib-scraper.io.config :as config]
            [lib-scraper.io.scrape :as scrape]
            [lib-scraper.scraper.core :as scraper]
            [lib-scraper.model.db :as mdb]))

(defn create-scrape
  ([config-file]
   (create-scrape config-file nil))
  ([config-file scrape-file]
   (let [scrape-file (cond
                       (some? scrape-file) scrape-file
                       (fs/directory? config-file) config-file
                       (fs/file? config-file) (fs/parent config-file))
         config (config/read-config config-file)]
     (scrape/write-scrape scrape-file config
                          (scraper/scrape config)))))

(defn query-scrape
  [scrape query & args]
  (let [{:keys [scrape ecosystem]} scrape]
    (apply mdb/q ecosystem query scrape args)))

(defn query-file
  [scrape-file query & args]
  (apply query-scrape (scrape/read-scrape scrape-file) query args))

(defn pull-scrape
  [scrape selector eid]
  (let [{:keys [scrape ecosystem]} scrape]
    (mdb/pull ecosystem scrape selector eid)))

(defn pull-file
  [scrape-file selector eid]
  (pull-scrape (scrape/read-scrape scrape-file) selector eid))
