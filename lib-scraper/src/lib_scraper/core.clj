(ns lib-scraper.core
  (:require [lib-scraper.scraper.core :refer [scrape]]))

(def skl-spec {:seed "https://scikit-learn.org/0.20/modules/classes.html"
               :should-visit '(and (match-url #".+\.html?(\?.*)?(#.*)?")
                                   (require-classes #{"reference" "internal"}))
               :visit (comp println type)
               :max-depth 1 ; restrict crawler for debugging purposes
               :max-pages 1}) ; restrict crawler for debugging purposes

(defn scrape-skl
  []
  (scrape skl-spec))
