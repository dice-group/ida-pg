(ns lib-scraper.scraper.core
  (:require [hickory.core :as h]
            [clojure.tools.logging :as log]
            [lib-scraper.crawler.core :as crawler]
            [lib-scraper.helpers.predicate :refer [p-and p-or p-expire p-log parse]]
            [lib-scraper.match.links :as l]
            [lib-scraper.scraper.traverser :refer [traverser]])
  (:import (edu.uci.ics.crawler4j.crawler Page)
           (edu.uci.ics.crawler4j.url WebURL)
           (edu.uci.ics.crawler4j.parser HtmlParseData)))

(defn- crawl
  "Preconfigured crawler."
  [{:keys [seed should-visit visit max-depth max-pages]
    :or {max-depth -1, max-pages -1}}]
  (crawler/crawl {:seed seed
                  :should-visit (p-or (l/match-url seed)
                                      (p-and should-visit
                                             (p-expire max-pages)))
                  :visit (fn [_, ^Page page]
                           (let [parse-data (.getParseData page)
                                 document (when (instance? HtmlParseData parse-data)
                                            (-> ^HtmlParseData parse-data
                                                (.getHtml) (h/parse) (h/as-hickory)))]
                             (when document
                               (log/info "Traverse page:" (-> page (.getWebURL) (.getURL)))
                               (visit document))))
                  :max-depth max-depth}))

(defn- parse-should-visit
  [expr]
  (if (fn? expr) expr
    (parse {'require-classes l/require-classes
            'match-url l/match-url
            're-pattern re-pattern}
           expr)))

(defn scrape
  [{:keys [should-visit hooks patterns] :as spec}]
  (let [{:keys [traverser conn]} (traverser hooks patterns)]
    (crawl (merge spec
                  {:should-visit (parse-should-visit should-visit)
                   :visit traverser}))
    @conn))
