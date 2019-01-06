(ns lib-scraper.model.paradigms.oo.core
  (:require [lib-scraper.model.syntax :refer [defparadigm]]
            [lib-scraper.model.concepts.namespace :refer [namespace]]
            [lib-scraper.model.concepts.parameter :refer [parameter]]
            [lib-scraper.model.concepts.datatype :refer [datatype]]
            [lib-scraper.model.paradigms.oo.class :refer [class]]
            [lib-scraper.model.paradigms.oo.constructor :refer [constructor]]
            [lib-scraper.model.paradigms.oo.method :refer [method]])
  (:refer-clojure :exclude [class namespace]))

(defparadigm oo
  :namespace namespace
  :class class
  :constructor constructor
  :method method
  :parameter parameter
  :datatype datatype)
