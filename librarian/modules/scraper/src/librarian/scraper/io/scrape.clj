(ns librarian.scraper.io.scrape
  "Creating scrape files via the library scraper."
  (:require [me.raynes.fs :as fs]
            [librarian.model.io.scrape :as mscrape]
            [librarian.scraper.io.config :as config]
            [librarian.scraper.core :as scraper]))

(defn create-scrape
  "Takes a configuration file or path, creates a scrape database and writes the scrape to disk.
   If there already exists a scrape for the given configuration and nothing about the crawler config changed, the existing scrape is updated cheaply without a full recrawl.
   This can be disabled by setting the boolean `:cache` configuration option to `false`."
  ([config-file &
    {:keys [out, ^boolean cache]
     :or {cache true}}]
   (let [out (cond
               (some? out) out
               (fs/directory? config-file) config-file
               (fs/file? config-file) (fs/parent config-file))
         config (config/read-config config-file)
         old-scrape (when cache
                      (try
                        (mscrape/read-scrape out)
                        (catch Exception e)))
         scrape (if (and old-scrape
                         (= (-> old-scrape :meta :librarian/cache-id)
                            (-> config :meta :librarian/cache-id)))
                  (scraper/update-cached-scrape config (:db old-scrape))
                  (scraper/scrape config))]
     (mscrape/write-scrape out config scrape))))
