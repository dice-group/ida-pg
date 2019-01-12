(ns lib-scraper.io.core
  (:require [me.raynes.fs :as fs]
            [lib-scraper.io.config :as config]
            [lib-scraper.io.scrape :as scrape]
            [lib-scraper.scraper.core :as scraper]))

(defn create-scrape
  ([config-file]
   (create-scrape config-file nil))
  ([config-file scrape-file]
   (let [scrape-file (cond
                       (some? scrape-file) scrape-file
                       (fs/directory? config-file) config-file
                       (fs/file? config-file) (fs/parent config-file))]
     (let [config (config/read-config config-file)]
       (scrape/write-scrape scrape-file config
                            (scraper/scrape config))))))
