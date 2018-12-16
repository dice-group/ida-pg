(ns lib-scraper.core
  (:require [hickory.select :as s]
            [lib-scraper.scraper.core :refer [scrape]]
            [lib-scraper.model.concepts.class :as class]
            [lib-scraper.model.concepts.function :as function]))

(def skl-spec {:seed "https://scikit-learn.org/0.20/modules/classes.html"
               :should-visit '(match-url #"https://scikit-learn\.org/0\.20/modules/.+BaseEstimator\.html.*")
               :max-depth 1 ; restrict crawler for debugging purposes
               :max-pages 1 ; restrict crawler for debugging purposes
               :patterns {:name {:selector [:children (s/tag :dt)
                                            :children (s/class :descname)]
                                 :limit 1}
                          :description {:attribute :description
                                        :selector [:children (s/tag :dd)
                                                   :children
                                                   (s/and (s/tag :p)
                                                          (s/not (s/class "rubric")))]}}
               :hooks [; concepts:
                       {:trigger :document
                        :concept ::class/concept
                        :selector [:descendants (s/and (s/tag :dl) (s/class :class))]}
                       {:trigger ::class/concept
                        :concept ::function/concept
                        :selector [:descendants (s/and (s/tag :dl) (s/class :method))]
                        :ref-from-trigger ::class/method}
                       ; names:
                       {:trigger ::class/concept
                        :attribute ::class/name
                        :pattern :name}
                       {:trigger ::function/concept
                        :attribute ::function/name
                        :pattern :name}
                       ; descriptions:
                       {:trigger ::class/concept, :pattern :description}
                       {:trigger ::function/concept, :pattern :description}]})

(defn scrape-skl
  []
  (scrape skl-spec))
