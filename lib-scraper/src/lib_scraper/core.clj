(ns lib-scraper.core
  (:require [hickory.select :as s]
            [lib-scraper.scraper.core :refer [scrape-and-store! load-stored]]
            [lib-scraper.model.concepts.package :as package]
            [lib-scraper.model.concepts.class :as class]
            [lib-scraper.model.concepts.function :as function]
            [lib-scraper.model.concepts.parameter :as parameter]
            [lib-scraper.model.concepts.datatype :as datatype]))

(def skl-spec {:seed "https://scikit-learn.org/0.20/modules/classes.html"
               :should-visit '(match-url #"https://scikit-learn\.org/0\.20/modules/generated/.*")
               :max-pages 1 ; restrict crawler for debugging purposes
               :patterns {:name {:selector [:children (s/tag :dt)
                                            :children (s/class :descname)]}
                          :description {:attribute :description
                                        :selector [:children (s/tag :dd)
                                                   :children
                                                   (s/and (s/tag :p)
                                                          (s/not (s/class "rubric")))]}}
               :hooks [; packages:
                       {:trigger [::class/concept ::function/concept]
                        :concept ::package/concept
                        :selector [:children (s/tag :dt)
                                   :children (s/class :descclassname)]
                        :ref-to-trigger ::package/member}
                       {:trigger ::package/concept
                        :attribute ::package/name
                        :transform #".*[^.]"}
                       ; classes:
                       {:trigger :document
                        :concept ::class/concept
                        :selector [:descendants (s/and (s/tag :dl) (s/class :class))]}
                       {:trigger ::class/concept
                        :attribute ::class/name
                        :pattern :name}
                       {:trigger ::class/concept, :pattern :description}
                       ; functions:
                       {:trigger :document
                        :concept ::function/concept
                        :selector [:descendants (s/and (s/tag :dl) (s/class :function))]}
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
                                   :children (s/tag :p)]}
                       ; datatypes:
                       {:trigger ::parameter/concept
                        :concept ::datatype/concept
                        :selector [:children (s/and (s/tag :span) (s/class :classifier))]
                        :ref-to-trigger ::datatype/instance}
                       {:trigger ::datatype/concept
                        :attribute ::datatype/name
                        :transform #"[A-Za-z]+"}]})

(defn scrape-skl!
  []
  (scrape-and-store! skl-spec "resources/scrapes/skl.edn")
  (load-stored "resources/scrapes/skl.edn"))
