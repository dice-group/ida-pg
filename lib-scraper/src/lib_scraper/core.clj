(ns lib-scraper.core
  (:require [hickory.select :as s]
            [lib-scraper.scraper.core :refer [scrape-and-store! load-stored]]
            [lib-scraper.model.core :as m]
            [lib-scraper.model.concepts.package :as package]
            [lib-scraper.model.concepts.class :as class]
            [lib-scraper.model.concepts.function :as function]
            [lib-scraper.model.concepts.parameter :as parameter]
            [lib-scraper.model.concepts.datatype :as datatype]))

(def skl-spec {:seed "https://scikit-learn.org/0.20/modules/classes.html"
               :should-visit '(match-url #"https://scikit-learn\.org/0\.20/modules/generated/.*")
               :max-pages 1 ; restrict crawler for debugging purposes
               :ecosystem (m/ecosystems :python)
               :patterns {:name {:selector [:children (s/tag :dt)
                                            :children (s/class :descname)]}
                          :description {:attribute :description
                                        :selector [:children (s/tag :dd)
                                                   :children
                                                   (s/and (s/tag :p)
                                                          (s/not (s/class "rubric")))]}
                          :parameter-info {:selector [:children (s/and (s/tag :span)
                                                                       (s/class :classifier))]}}
               :hooks [; packages:
                       {:trigger [:class :function]
                        :concept :package
                        :selector [:children (s/tag :dt)
                                   :children (s/class :descclassname)]
                        :ref-to-trigger ::package/member}
                       {:trigger :package
                        :attribute ::package/name
                        :transform #".*[^.]"}
                       ; classes:
                       {:trigger :document
                        :concept :class
                        :selector [:descendants (s/and (s/tag :dl) (s/class :class))]}
                       {:trigger :class
                        :attribute ::class/name
                        :pattern :name}
                       {:trigger :class, :pattern :description}
                       ; functions:
                       {:trigger :document
                        :concept :function
                        :selector [:descendants (s/and (s/tag :dl) (s/class :function))]}
                       {:trigger :class
                        :concept :function
                        :selector [:descendants (s/and (s/tag :dl) (s/class :method))]
                        :ref-from-trigger ::class/method}
                       {:trigger :function
                        :attribute ::function/name
                        :pattern :name}
                       {:trigger :function, :pattern :description}
                       ; parameters:
                       {:trigger :function
                        :concept :parameter
                        :selector [:descendants
                                   (s/and (s/tag :th) (s/find-in-text #"Parameters"))
                                   [:ancestors :select (s/tag :tr) :limit 1]
                                   :descendants (s/tag :dt)]
                        :ref-from-trigger ::function/parameter}
                       {:trigger :parameter
                        :attribute ::parameter/name
                        :selector [:children (s/tag :strong)]}
                       {:trigger :parameter
                        :attribute ::parameter/position
                        :value :trigger-index}
                       {:trigger :parameter
                        :attribute :description
                        :selector [[:following-siblings :select (s/tag :dd) :limit 1]
                                   :children (s/tag :p)]}
                       {:trigger :parameter
                        :attribute ::parameter/optional
                        :pattern :parameter-info
                        :transform #(clojure.string/includes? % "optional")}
                       ; datatypes:
                       {:trigger :parameter
                        :concept :datatype
                        :ref-to-trigger ::datatype/instance
                        :pattern :parameter-info}
                       {:trigger :datatype
                        :attribute ::datatype/name
                        :transform #"^[A-Za-z]+"}]})

(defn scrape-skl!
  []
  (scrape-and-store! skl-spec "resources/scrapes/skl.edn")
  (load-stored "resources/scrapes/skl.edn"))
