(ns lib-scraper.model.ecosystems.python.core
  (:require [lib-scraper.model.syntax :refer [defecosystem]]
            [lib-scraper.model.concepts.callable :refer [callable]]
            [lib-scraper.model.paradigms.oo.core :refer [oo]]
            [lib-scraper.model.paradigms.functional.core :refer [functional]]
            [lib-scraper.model.ecosystems.python.class :refer [class]])
  (:refer-clojure :exclude [class]))

(defecosystem python [oo functional]
  :callable callable
  :class class)

python
