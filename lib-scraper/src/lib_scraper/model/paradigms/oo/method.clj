(ns lib-scraper.model.paradigms.oo.method
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.model.concepts.named :refer [named]]
            [lib-scraper.model.concepts.callable :refer [callable]]
            [lib-scraper.model.paradigms.oo.class :as class]))

(defconcept method [named callable]
  :spec ::method)

(s/def ::method (hs/entity-keys :req [::class/_method]))
