(ns lib-scraper.model.ecosystems.python
  (:require [lib-scraper.helpers.transaction :refer [merge-fn-maps]]
            [lib-scraper.model.paradigms.oo :as oo]
            [lib-scraper.model.paradigms.functional :as functional]))

(def ecosystem {:concept (merge oo/concept
                                functional/concept)
                :postprocess (merge-fn-maps oo/postprocess
                                            functional/postprocess)})
