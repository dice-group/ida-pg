(ns repl-tools
  (:require [lib-scraper.io.core :refer [create-scrape]]
            [lib-scraper.io.scrape :refer [read-scrape]]))

(def show-scrape (comp :scrape read-scrape create-scrape))
