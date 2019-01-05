(ns lib-scraper.model.paradigms.functional.function
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.model.concepts.named :refer [named]]
            [lib-scraper.model.concepts.callable :refer [callable]]
            [lib-scraper.model.concepts.package :as package]))

(s/def ::function (hs/entity-keys :req [::package/_member]))

(defconcept function [named callable]
  :spec ::function)
