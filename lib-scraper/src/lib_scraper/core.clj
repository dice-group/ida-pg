(ns lib-scraper.core
  (:require [hickory.select :as s]
            [lib-scraper.scraper.core :refer [scrape]]
            [lib-scraper.model.concepts.class :as class]
            [lib-scraper.model.concepts.function :as function]
            [lib-scraper.model.concepts.parameter :as parameter]))

(def skl-spec {:seed "https://scikit-learn.org/0.20/modules/classes.html"
               :should-visit '(match-url #"https://scikit-learn\.org/0\.20/modules/.+BiclusterMixin\.html.*")
               :max-depth 1 ; restrict crawler for debugging purposes
               :max-pages 1 ; restrict crawler for debugging purposes
               :patterns {:name {:selector [:children (s/tag :dt)
                                            :children (s/class :descname)]}
                          :description {:attribute :description
                                        :selector [:children (s/tag :dd)
                                                   :children
                                                   (s/and (s/tag :p)
                                                          (s/not (s/class "rubric")))]}}
               :hooks [; classes:
                       {:trigger :document
                        :concept ::class/concept
                        :selector [:descendants (s/and (s/tag :dl) (s/class :class))]}
                       {:trigger ::class/concept
                        :attribute ::class/name
                        :pattern :name}
                       {:trigger ::class/concept, :pattern :description}
                       ; functions:
                       {:trigger ::class/concept
                        :concept ::function/concept
                        :selector [:descendants (s/and (s/tag :dl) (s/class :method))]
                        :ref-from-trigger ::class/method}
                       {:trigger ::function/concept
                        :attribute ::function/name
                        :pattern :name}
                       {:trigger ::function/concept, :pattern :description}
                       ; parameters:
                       {:trigger ::function/concept
                        :concept ::parameter/concept
                        :selector [:descendants
                                   (s/and (s/tag :th) (s/find-in-text #"Parameters"))
                                   [:ancestors :select (s/tag :tr) :limit 1]
                                   :descendants (s/tag :dt)]
                        :ref-from-trigger ::function/param}
                       {:trigger ::parameter/concept
                        :attribute ::parameter/name
                        :selector [:children (s/tag :strong)]}
                       {:trigger ::parameter/concept
                        :attribute ::parameter/position
                        :value :trigger-index}
                       {:trigger ::parameter/concept
                        :attribute :description
                        :selector [[:following-siblings :select (s/tag :dd) :limit 1]
                                   :children (s/tag :p)]}]})

(defn scrape-skl
  []
  (scrape skl-spec))
