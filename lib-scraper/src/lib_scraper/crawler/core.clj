(ns lib-scraper.crawler.core
  (:require [me.raynes.fs :as fs]
            [lib-scraper.crawler.factory])
  (:import (edu.uci.ics.crawler4j.crawler CrawlConfig CrawlController)
           (edu.uci.ics.crawler4j.fetcher PageFetcher)
           (edu.uci.ics.crawler4j.robotstxt RobotstxtConfig
                                            RobotstxtServer)
           (lib_scraper.crawler Factory)))

(def crawler-storage (str (fs/temp-dir "lib-scraper-data")))

(defn crawl
  [{:keys [^String seed
           should-visit visit
           ^int concurrency
           ^String storage-folder
           ^int max-depth
           ^int politeness-delay
           ^boolean binary-content
           ^boolean resumable
           ^int monitor-delay
           ^int shutdown-delay
           ^int cleanup-delay]
    :or {concurrency 1
         storage-folder crawler-storage
         max-depth -1
         politeness-delay 20
         binary-content false
         resumable false
         monitor-delay 2
         shutdown-delay 1
         cleanup-delay 1}}]
  (let [config (doto (CrawlConfig.)
                 (.setCrawlStorageFolder storage-folder)
                 (.setMaxDepthOfCrawling max-depth)
                 (.setPolitenessDelay politeness-delay)
                 (.setIncludeBinaryContentInCrawling binary-content)
                 (.setResumableCrawling resumable)
                 (.setThreadMonitoringDelaySeconds monitor-delay)
                 (.setThreadShutdownDelaySeconds shutdown-delay)
                 (.setCleanupDelaySeconds cleanup-delay))
        fetcher (PageFetcher. config)
        robots-config (RobotstxtConfig.)
        robots-server (RobotstxtServer. robots-config fetcher)
        controller (CrawlController. config fetcher robots-server)
        crawler-factory (Factory. {:should-visit should-visit
                                   :visit visit})]
    (.addSeed controller seed)
    (.start controller crawler-factory concurrency)
    (when-not resumable
      (fs/delete-dir crawler-storage))))
