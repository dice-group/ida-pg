(ns lib-scraper.model.paradigms.oo.core
  (:require [lib-scraper.model.syntax :refer [defparadigm]]
            [lib-scraper.model.paradigms.common :refer [common]]
            [lib-scraper.model.paradigms.oo.class :refer [class]]
            [lib-scraper.model.paradigms.oo.constructor :refer [constructor]]
            [lib-scraper.model.paradigms.oo.method :refer [method]])
  (:refer-clojure :exclude [class]))

(defparadigm oo [common]
  :class class
  :constructor constructor
  :method method)
