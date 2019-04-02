(ns librarian.scraper.io.scrape
  (:require [me.raynes.fs :as fs]
            [librarian.model.io.scrape :as mscrape]
            [librarian.scraper.io.config :as config]
            [librarian.scraper.core :as scraper]))

(defn create-scrape
  ([config-file]
   (create-scrape config-file nil))
  ([config-file scrape-file]
   (let [scrape-file (cond
                       (some? scrape-file) scrape-file
                       (fs/directory? config-file) config-file
                       (fs/file? config-file) (fs/parent config-file))
         config (config/read-config config-file)]
     (mscrape/write-scrape scrape-file config
                          (scraper/scrape config)))))
