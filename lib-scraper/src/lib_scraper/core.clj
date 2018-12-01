(ns lib-scraper.core
  (:require [lib-scraper.scraper.core :refer [scrape]]))

(def skl-spec {:seed "https://scikit-learn.org/0.20/modules/classes.html"
               :should-visit '(match-url #"https://scikit-learn\.org/0\.20/modules/.+BaseEstimator\.html.*")
               :max-depth 1 ; restrict crawler for debugging purposes
               :max-pages 1 ; restrict crawler for debugging purposes
               :hooks {:document {}}})

(defn scrape-skl
  []
  (scrape skl-spec))
