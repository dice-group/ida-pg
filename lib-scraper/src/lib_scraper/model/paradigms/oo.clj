(ns lib-scraper.model.paradigms.oo
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.concepts.common :as common]
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

(def postprocess {::class/concept class/postprocess})

(s/def ::function (s/and ::function/concept
                         (hs/entity-keys :req [(or ::class/_method
                                                   ::class/_constructor)])))
