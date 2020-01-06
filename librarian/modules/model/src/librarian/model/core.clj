(ns librarian.model.core
  "Contains the ecosystems that are included in the librarian model by default."
  (:require [librarian.model.ecosystems.python.core :refer [python]]))

(def model-version "2.0.0")

(defn assoc-eco
  [map key eco]
  (assoc map key (assoc eco
                        :alias key
                        :version model-version)))

(def ecosystems (assoc-eco {} :python python))
