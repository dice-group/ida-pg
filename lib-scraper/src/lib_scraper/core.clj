(ns lib-scraper.core
  (:import (edu.uci.ics.crawler4j.crawler CrawlConfig CrawlController
                                          Page WebCrawler)
           (edu.uci.ics.crawler4j.fetcher PageFetcher)
           (edu.uci.ics.crawler4j.robotstxt RobotstxtConfig
                                            RobotstxtServer)
           (edu.uci.ics.crawler4j.parser HtmlParser)
           (edu.uci.ics.crawler4j.url WebURL)))

(defn crawl-skl
  []
  (let [config (doto (CrawlConfig.)
                     (.setCrawlStorageFolder "./crawl")
                     (.setMaxDepthOfCrawling 1)
                     (.setPolitenessDelay 100))
        fetcher (PageFetcher. config)
        robots-config (RobotstxtConfig.)
        robots-server (RobotstxtServer. robots-config fetcher)
        controller (CrawlController. config fetcher robots-server)]
    (.addSeed controller "http://scikit-learn.org/0.20/modules/classes.html")
    (.start controller WebCrawler 1)))
