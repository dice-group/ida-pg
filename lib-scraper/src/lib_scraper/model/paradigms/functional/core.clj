(ns lib-scraper.model.paradigms.functional.core
  (:require [lib-scraper.model.syntax :refer [defconcept defparadigm]]
            [lib-scraper.model.concepts.namespace :refer [namespace]]
            [lib-scraper.model.concepts.parameter :refer [parameter]]
            [lib-scraper.model.concepts.datatype :refer [datatype]]
            [lib-scraper.model.paradigms.functional.function :refer [function]])
  (:refer-clojure :exclude [namespace]))

(defparadigm functional
  :namespace namespace
  :function function
  :parameter parameter
  :datatype datatype)
