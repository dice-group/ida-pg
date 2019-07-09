(ns librarian.scraper.core
  (:require [hickory.core :as h]
            [clojure.tools.logging :as log]
            [datascript.core :as d]
            [librarian.scraper.crawler.core :as crawler]
            [librarian.helpers.predicate :refer [p-and p-or p-expire parse]]
            [librarian.model.db :as mdb]
            [librarian.model.concepts.snippet :as snippet]
            [librarian.scraper.match :as match]
            [librarian.scraper.traverser :refer [traverser]])
  (:import (edu.uci.ics.crawler4j.crawler Page)
           (edu.uci.ics.crawler4j.url WebURL)
           (edu.uci.ics.crawler4j.parser HtmlParseData)))

(defn- crawl
  "Preconfigured crawler."
  [{:keys [seed should-visit visit max-depth max-pages]
    :or {max-depth -1, max-pages -1}}]
  (crawler/crawl {:seed seed
                  :should-visit (p-or (match/match-url seed)
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
  [{:keys [name should-visit] :as config}]
  (let [{:keys [traverser finalize]} (traverser config)]
    (log/info (str "Starting scrape " name "..."))
    (crawl (merge config
                  {:should-visit should-visit
                   :visit traverser}))
    (log/info "Finalizing scrape " name "...")
    (let [result (finalize)]
      (log/info (str "Successfully scraped " name "."))
      result)))

(defn update-cached-scrape
  [{:keys [name snippets]} db]
  (log/info (str "Updating cached scrape " name "..."))
  (let [snippet-datoms (d/datoms db :avet :type ::snippet/snippet)
        db (d/db-with db (mapv #(vector :db/retractEntity (:e %))
                               snippet-datoms))
        db (mdb/with-seq db snippets)]
    (log/info (str "Successfully updated cached scrape " name
                   ". Removed snippets: " (count snippet-datoms)
                   ", added snippets: " (count snippets) "."))
    db))
