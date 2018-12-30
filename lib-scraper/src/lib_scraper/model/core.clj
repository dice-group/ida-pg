(ns lib-scraper.model.core
  (:require [datascript.core :as d]
            [lib-scraper.model.ecosystems.python :as python]))

(def ecosystems {:python python/ecosystem})

(defn conn
  [ecosystem]
  (d/create-conn (:concept ecosystem)))
