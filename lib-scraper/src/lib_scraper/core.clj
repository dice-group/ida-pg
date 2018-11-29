(ns lib-scraper.core
  (:require [lib-scraper.crawler.core :refer [crawl]]
            [lib-scraper.helpers.predicate :refer [p-and p-or p-expire p-log]]
            [lib-scraper.match.links :as l])
  (:import (edu.uci.ics.crawler4j.crawler Page)
           (edu.uci.ics.crawler4j.url WebURL)))

(defn crawl-skl
  []
  (let [seed "http://scikit-learn.org/0.20/modules/classes.html"]
    (crawl {:seed seed
            :should-visit (p-and (p-expire 3)
                                 (p-or (l/match-url seed)
                                       (l/require-classes #{"reference" "internal"}))
                                 (p-log (fn [_, ^Page page, ^WebURL url]
                                          (str url (into {} (.getAttributes url))))))
            :visit (fn [_, ^Page page])
                     ;(println (.getHtml (.getParseData page))))
            :max-depth 1})))
