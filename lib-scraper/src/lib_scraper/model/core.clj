(ns lib-scraper.model.core
  (:require [lib-scraper.model.ecosystems.python.core :refer [python]]))

(def model-version "1.0.0")

(defn assoc-eco
  [map key eco]
  (assoc map key (assoc eco
                        :alias key
                        :version model-version)))

(def ecosystems (assoc-eco {} :python python))
