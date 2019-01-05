(ns lib-scraper.model.paradigms.functional.core
  (:require [lib-scraper.model.syntax :refer [defconcept defparadigm]]
            [lib-scraper.model.concepts.package :as package :refer [package]]
            [lib-scraper.model.concepts.parameter :refer [parameter]]
            [lib-scraper.model.concepts.datatype :refer [datatype]]
            [lib-scraper.model.paradigms.functional.function :refer [function]]))

(defparadigm functional
  :package package
  :function function
  :parameter parameter
  :datatype datatype)
