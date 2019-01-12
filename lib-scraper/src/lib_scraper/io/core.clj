(ns lib-scraper.io.core
  (:require [hickory.select :as s]
            [clojure.tools.logging :as log]
            [me.raynes.fs :as fs]
            [lib-scraper.scraper.core :as scraper]
            [lib-scraper.io.config :as config]
            [lib-scraper.io.scrape :as scrape])
  (:gen-class))

(def default-config-name config/default-config-name)
(def default-scrape-name "scrape.db")

(defn create-scrape
  ([config-path]
   (create-scrape config-path nil))
  ([config-path scrape-path]
   (let [config-file (cond
                       (fs/directory? config-path)
                       (fs/file config-path default-config-name)
                       (fs/file? config-path) (fs/file config-path)
                       :else (throw (Error. "Invalid config path.")))
         scrape-file (cond
                       (nil? scrape-path)
                       (fs/file (fs/parent config-file) default-scrape-name)
                       (fs/file? scrape-path) (fs/file scrape-path)
                       (fs/directory? scrape-path)
                       (fs/file scrape-path default-scrape-name)
                       :else (throw (Error. "Invalid scrape path.")))]
     (log/info (str "Reading config from " config-file "..."))
     (let [config (config/read-config config-file)]
       (log/info (str "Successfully read config " (:name config) "."))
       (log/info "Starting scrape...")
       (scrape/write-scrape scrape-file config
                            (scraper/scrape config))
       (log/info (str "Success! Scrape was written to " scrape-file "."))))))

(defn load-scrape
  [scrape-path]
  (let [scrape-file (if (fs/directory? scrape-path)
                      (fs/file scrape-path default-scrape-name)
                      (fs/file scrape-path))]
    (scrape/read-scrape scrape-file)))
