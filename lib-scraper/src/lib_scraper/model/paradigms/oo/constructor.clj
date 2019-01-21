(ns lib-scraper.model.paradigms.oo.constructor
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.model.paradigms.oo.class :as class]
            [lib-scraper.model.concepts.callable :refer [callable]]))

(defconcept constructor [callable]
  :spec ::constructor)

(s/def ::constructor (hs/entity-keys :req [::class/_constructor]))
