(ns lib-scraper.model.ecosystems.python.core
  (:require [lib-scraper.model.syntax :refer [defecosystem]]
            [lib-scraper.model.paradigms.oo.core :refer [oo]]
            [lib-scraper.model.paradigms.functional.core :refer [functional]]
            [lib-scraper.model.ecosystems.python.class :refer [class]]
            [lib-scraper.model.ecosystems.python.constructor :refer [constructor]])
  (:refer-clojure :exclude [class]))

(defecosystem python [oo functional]
  :class class
  :constructor constructor)
