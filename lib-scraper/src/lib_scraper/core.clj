(ns lib-scraper.core
  (:require [hickory.select :as s]
            [lib-scraper.scraper.core :refer [scrape-and-store! load-stored]]
            [lib-scraper.model.core :as m]
            [lib-scraper.model.concepts.package :as package]
            [lib-scraper.model.concepts.named :as named]
            [lib-scraper.model.concepts.callable :as callable]
            [lib-scraper.model.paradigms.oo.class :as class]
            [lib-scraper.model.paradigms.oo.method :as method]
            [lib-scraper.model.paradigms.functional.function :as function]
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
                       {:trigger [::class/class ::function/function]
                        :concept ::package/package
                        :selector [:children (s/tag :dt)
                                   :children (s/class :descclassname)]
                        :ref-to-trigger ::package/member}
                       {:trigger ::package/package
                        :attribute ::named/name
                        :transform #".*[^.]"}
                       ; classes:
                       {:trigger :document
                        :concept ::class/class
                        :selector [:descendants (s/and (s/tag :dl) (s/class :class))]}
                       {:trigger ::class/class
                        :attribute ::named/name
                        :pattern :name}
                       {:trigger ::class/class, :pattern :description}
                       ; callables:
                       {:trigger :document
                        :concept ::function/function
                        :selector [:descendants (s/and (s/tag :dl) (s/class :function))]}
                       {:trigger ::class/class
                        :concept ::method/method
                        :selector [:descendants (s/and (s/tag :dl) (s/class :method))]
                        :ref-from-trigger ::class/method}
                       {:trigger ::method/method
                        :attribute ::named/name
                        :pattern :name}
                       {:trigger ::function/function
                        :attribute ::named/name
                        :pattern :name}
                       {:trigger ::callable/callable, :pattern :description}
                       ; parameters:
                       {:trigger ::callable/callable
                        :concept ::parameter/parameter
                        :selector [:descendants
                                   (s/and (s/tag :th) (s/find-in-text #"Parameters"))
                                   [:ancestors :select (s/tag :tr) :limit 1]
                                   :descendants (s/tag :dt)]
                        :ref-from-trigger ::callable/parameter}
                       {:trigger ::parameter/parameter
                        :attribute ::named/name
                        :selector [:children (s/tag :strong)]}
                       {:trigger ::parameter/parameter
                        :attribute ::parameter/position
                        :value :trigger-index}
                       {:trigger ::parameter/parameter
                        :attribute :description
                        :selector [[:following-siblings :select (s/tag :dd) :limit 1]
                                   :children (s/tag :p)]}
                       {:trigger ::parameter/parameter
                        :attribute ::parameter/optional
                        :pattern :parameter-info
                        :transform #(clojure.string/includes? % "optional")}
                       ; datatypes:
                       {:trigger ::parameter/parameter
                        :concept ::datatype/datatype
                        :ref-to-trigger ::datatype/instance
                        :pattern :parameter-info}
                       {:trigger ::datatype/datatype
                        :attribute ::named/name
                        :transform #"^[A-Za-z]+"}]})

(defn scrape-skl!
  []
  (scrape-and-store! skl-spec "resources/scrapes/skl.edn")
  (load-stored "resources/scrapes/skl.edn"))
