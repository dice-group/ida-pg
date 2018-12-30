(ns lib-scraper.model.ecosystems.python
  (:require [lib-scraper.model.paradigms.oo :as oo]
            [lib-scraper.model.paradigms.functional :as functional]))

(def ecosystem {:concept (merge oo/concept
                                functional/concept)})
