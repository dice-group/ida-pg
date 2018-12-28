(ns lib-scraper.model.core
  (:require [datascript.core :as d]
            [lib-scraper.model.concepts.core :as concepts]))

(defn conn
  []
  (d/create-conn concepts/spec))
