(ns librarian.scraper.io.scrape
  (:require [me.raynes.fs :as fs]
            [librarian.model.io.scrape :as mscrape]
            [librarian.scraper.io.config :as config]
            [librarian.scraper.core :as scraper]))

(defn create-scrape
  ([config-file]
   (create-scrape config-file nil nil))
  ([config-file scrape-file]
   ; allow omitting scrape-file and providing config map:
   (if (map? scrape-file)
     (create-scrape config-file nil scrape-file)
     (create-scrape config-file scrape-file nil)))
  ([config-file scrape-file
    {:keys [^boolean cache]
     :or {cache true}}]
   (let [scrape-file (cond
                       (some? scrape-file) scrape-file
                       (fs/directory? config-file) config-file
                       (fs/file? config-file) (fs/parent config-file))
         config (config/read-config config-file)
         old-scrape (when cache
                      (try
                        (mscrape/read-scrape scrape-file)
                        (catch Exception e)))
         scrape (if (and old-scrape
                         (= (-> old-scrape :meta :librarian/cache-id)
                            (-> config :meta :librarian/cache-id)))
                  (scraper/update-cached-scrape config (:db old-scrape))
                  (scraper/scrape config))]
     (mscrape/write-scrape scrape-file config scrape))))
