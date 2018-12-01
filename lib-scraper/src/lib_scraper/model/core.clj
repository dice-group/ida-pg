(ns lib-scraper.model.core
  (:require [datascript.core :as d]
            [lib-scraper.model.concepts.core :refer [concepts]]))

(defn create-db
  []
  (d/create-conn concepts))
