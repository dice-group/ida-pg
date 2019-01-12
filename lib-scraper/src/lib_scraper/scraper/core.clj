(ns lib-scraper.scraper.core
  (:require [hickory.core :as h]
            [clojure.tools.logging :as log]
            [lib-scraper.crawler.core :as crawler]
            [lib-scraper.helpers.predicate :refer [p-and p-or p-expire parse]]
            [lib-scraper.scraper.match :as m]
            [lib-scraper.scraper.traverser :refer [traverser]])
  (:import (edu.uci.ics.crawler4j.crawler Page)
           (edu.uci.ics.crawler4j.url WebURL)
           (edu.uci.ics.crawler4j.parser HtmlParseData)))

(defn- crawl
  "Preconfigured crawler."
  [{:keys [seed should-visit visit max-depth max-pages]
    :or {max-depth -1, max-pages -1}}]
  (crawler/crawl {:seed seed
                  :should-visit (p-or (m/match-url seed)
                                      (p-and should-visit (p-expire max-pages)))
                  :visit (fn [_, ^Page page]
                           (let [parse-data (.getParseData page)
                                 url (-> page (.getWebURL) (.getURL))
                                 document (when (instance? HtmlParseData parse-data)
                                            (-> ^HtmlParseData parse-data
                                                (.getHtml) (h/parse) (h/as-hickory)))]
                             (when document
                               (log/info "Traverse page:" url)
                               (visit document url))))
                  :max-depth max-depth
                  :concurrency (let [p (.availableProcessors (Runtime/getRuntime))]
                                 (if (or (< max-pages 0) (>= max-pages p))
                                   p (inc max-pages)))}))

(defn scrape
  [{:keys [should-visit] :as spec}]
  (let [{:keys [traverser finalize]} (traverser spec)]
    (crawl (merge spec
                  {:should-visit should-visit
                   :visit traverser}))
    (finalize)))
