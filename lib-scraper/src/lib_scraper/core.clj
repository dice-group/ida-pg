(ns lib-scraper.core
  (:require [hickory.select :as s]
            [lib-scraper.scraper.core :refer [scrape]]
            [lib-scraper.model.concepts.class :as class]))

(def skl-spec {:seed "https://scikit-learn.org/0.20/modules/classes.html"
               :should-visit '(match-url #"https://scikit-learn\.org/0\.20/modules/.+BaseEstimator\.html.*")
               :max-depth 1 ; restrict crawler for debugging purposes
               :max-pages 1 ; restrict crawler for debugging purposes
               :hooks [{:trigger :document
                        :concept ::class/concept
                        :selector (s/descendant (s/and (s/tag :dl) (s/class :class)))}
                       {:trigger ::class/concept
                        :attribute ::class/name
                        :selector (s/descendant (s/tag :dt) (s/class :descname))
                        :limit 1}
                       {:trigger ::class/concept
                        :attribute :description
                        :selector (s/descendant (s/tag :dd) (s/tag :p))
                        :limit 1}]})

(defn scrape-skl
  []
  (scrape skl-spec))
