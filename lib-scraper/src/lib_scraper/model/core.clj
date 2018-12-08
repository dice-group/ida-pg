(ns lib-scraper.model.core
  (:require [datascript.core :as d]
            [lib-scraper.model.concepts.core :refer [concepts]]))

(defn conn
  []
  (d/create-conn concepts))
