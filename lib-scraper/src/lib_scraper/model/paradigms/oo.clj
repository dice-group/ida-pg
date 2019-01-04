(ns lib-scraper.model.paradigms.oo
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.syntax :refer [defparadigm]]
            [lib-scraper.model.concepts.package :refer [package]]
            [lib-scraper.model.concepts.class :as class :refer [class]]
            [lib-scraper.model.concepts.function :as function :refer [function]]
            [lib-scraper.model.concepts.parameter :refer [parameter]]
            [lib-scraper.model.concepts.datatype :refer [datatype]])
  (:refer-clojure :exclude [class]))

(defparadigm oo
  :package package
  :class class
  :function {:concept function
             :spec ::function}
  :parameter parameter
  :datatype datatype)

(s/def ::function (s/and (function :spec)
                         (hs/entity-keys :req [(or ::class/_method
                                                   ::class/_constructor)])))
