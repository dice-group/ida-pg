(ns librarian.scraper.crawler.factory
  "A Clojure implementation of crawler4j's abstract WebCrawlerFactory.
   Allows the usage of Clojure functions as implementations of visit and shouldVisit."
  (:gen-class :name librarian.scraper.crawler.Factory
              :implements [edu.uci.ics.crawler4j.crawler.CrawlController$WebCrawlerFactory]
              :prefix "crawler-"
              :state methods
              :init init
              :constructors {[clojure.lang.IPersistentMap] []})
  (:import (edu.uci.ics.crawler4j.crawler Page WebCrawler
                                          CrawlController$WebCrawlerFactory)
           (edu.uci.ics.crawler4j.url WebURL)))

(defn crawler-init
  [^clojure.lang.IPersistentMap methods]
  [[] methods])

(defn crawler-newInstance
  [this]
  (let [[should-visit visit] ((juxt :should-visit :visit) (.methods this))]
    (proxy [WebCrawler] []
      (shouldVisit ^boolean [^Page referrer, ^WebURL url]
        (should-visit this referrer url))
      (visit ^void [^Page page]
        (visit this page)))))
