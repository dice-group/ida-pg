(ns lib-scraper.model.paradigms.oo
  (:require [lib-scraper.model.concepts.common :as common]
            [lib-scraper.model.concepts.package :as package]
            [lib-scraper.model.concepts.class :as class]
            [lib-scraper.model.concepts.function :as function]
            [lib-scraper.model.concepts.parameter :as parameter]
            [lib-scraper.model.concepts.datatype :as datatype]))

(def concept (merge common/concept
                    package/concept
                    class/concept
                    function/concept
                    parameter/concept
                    datatype/concept))
