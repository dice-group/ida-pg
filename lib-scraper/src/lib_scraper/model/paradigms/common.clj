(ns lib-scraper.model.paradigms.common
  (:require [lib-scraper.model.syntax :refer [defparadigm]]
            [lib-scraper.model.concepts.named :refer [named]]
            [lib-scraper.model.concepts.callable :refer [callable]]
            [lib-scraper.model.concepts.parameter :refer [parameter]]
            [lib-scraper.model.concepts.datatype :refer [datatype]]
            [lib-scraper.model.concepts.namespace :refer [namespace]]
            [lib-scraper.model.concepts.namespaced :refer [namespaced]])
  (:refer-clojure :exclude [namespace]))

(defparadigm common
  :named named
  :callable callable
  :parameter parameter
  :datatype datatype
  :namespace namespace
  :namespaced namespaced)
