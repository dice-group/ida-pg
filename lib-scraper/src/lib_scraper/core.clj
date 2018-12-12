(ns lib-scraper.core
  (:require [hickory.select :as s]
            [lib-scraper.scraper.core :refer [scrape]]
            [lib-scraper.model.concepts.class :as class]
            [lib-scraper.model.concepts.function :as function]))

(def skl-spec {:seed "https://scikit-learn.org/0.20/modules/classes.html"
               :should-visit '(match-url #"https://scikit-learn\.org/0\.20/modules/.+BaseEstimator\.html.*")
               :max-depth 1 ; restrict crawler for debugging purposes
               :max-pages 1 ; restrict crawler for debugging purposes
               :patterns {:name {:selector (s/child (s/tag :dt) (s/class :descname))
                                 :limit 1}
                          :desc {:attribute :description
                                 :selector (s/child (s/tag :dd) (s/tag :p))
                                 :limit 1}}
               :hooks [; concepts:
                       {:trigger :document
                        :concept ::class/concept
                        :selector (s/descendant (s/and (s/tag :dl) (s/class :class)))}
                       {:trigger ::class/concept
                        :concept ::function/concept
                        :selector (s/descendant (s/and (s/tag :dl) (s/class :method)))
                        :ref-from-trigger ::class/method}
                       ; names:
                       {:trigger ::class/concept
                        :attribute ::class/name
                        :pattern :name}
                       {:trigger ::function/concept
                        :attribute ::function/name
                        :pattern :name}
                       ; descriptions:
                       {:trigger ::class/concept, :pattern :desc}
                       {:trigger ::function/concept, :pattern :desc}]})

(defn scrape-skl
  []
  (scrape skl-spec))
