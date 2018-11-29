(ns lib-scraper.core
  (:require [hickory.core :refer [parse]]
            [hickory.select :as s]
            [lib-scraper.crawler.core :refer [crawl]]
            [lib-scraper.helpers.predicate :refer [p-and p-or p-expire p-log]]
            [lib-scraper.match.links :as l])
  (:import (edu.uci.ics.crawler4j.crawler Page)
           (edu.uci.ics.crawler4j.url WebURL)
           (edu.uci.ics.crawler4j.parser HtmlParseData)))

(defn crawl-docs
  [seed should-visit visit]
  (crawl {:seed seed
          :should-visit (p-or (l/match-url seed)
                              (p-and (p-expire 1)
                                     should-visit
                                     (p-log (fn [_, ^Page page, ^WebURL url]
                                              (str "traversed: " url)))))
          :visit (fn [_, ^Page page]
                   (let [parse-data (.getParseData page)
                         document (when (instance? HtmlParseData parse-data)
                                    (parse (.getHtml ^HtmlParseData parse-data)))]
                     (when document
                       (visit document))))
          :max-depth 1}))

(defn crawl-skl
  []
  (crawl-docs "https://scikit-learn.org/0.20/modules/classes.html"
              (l/require-classes #{"reference" "internal"})
              (comp println type)))
