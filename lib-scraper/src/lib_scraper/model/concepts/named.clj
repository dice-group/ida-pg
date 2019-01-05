(ns lib-scraper.model.concepts.named
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.syntax :refer [defconcept]]))

(defconcept named
  :attributes {::name {:db/doc "Name of the concept."}}
  :spec ::named)

(s/def ::named (hs/entity-keys :req [::name]))
(s/def ::name string?)
