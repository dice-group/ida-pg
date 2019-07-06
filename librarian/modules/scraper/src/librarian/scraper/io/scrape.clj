(ns librarian.scraper.io.scrape
  (:require [me.raynes.fs :as fs]
            [librarian.model.io.scrape :as mscrape]
            [librarian.scraper.io.config :as config]
            [librarian.scraper.core :as scraper]))

(defn create-scrape
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
