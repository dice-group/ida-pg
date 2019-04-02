(ns repl-tools
  (:require [librarian.scraper.io.scrape :refer [create-scrape]]
            [librarian.model.io.scrape :refer [read-scrape]]))

(def show-scrape (comp :db read-scrape create-scrape))
