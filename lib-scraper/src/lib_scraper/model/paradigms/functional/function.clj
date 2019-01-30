(ns lib-scraper.model.paradigms.functional.function
  (:require [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.model.concepts.namespaced :refer [namespaced]]
            [lib-scraper.model.concepts.callable :refer [callable]]))

(defconcept function [namespaced callable])
