(ns lib-scraper.model.paradigms.functional.core
  (:require [lib-scraper.model.syntax :refer [defparadigm]]
            [lib-scraper.model.paradigms.common :refer [common]]
            [lib-scraper.model.paradigms.functional.function :refer [function]]))

(defparadigm functional [common]
  :function function)
