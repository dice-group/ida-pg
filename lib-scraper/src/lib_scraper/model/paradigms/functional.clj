(ns lib-scraper.model.paradigms.functional
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.syntax :refer [defparadigm]]
            [lib-scraper.model.concepts.package :as package :refer [package]]
            [lib-scraper.model.concepts.function :refer [function]]
            [lib-scraper.model.concepts.parameter :refer [parameter]]
            [lib-scraper.model.concepts.datatype :refer [datatype]]))

(defparadigm functional
  :package package
  :function {:concept function
             :spec ::function}
  :parameter parameter
  :datatype datatype)

(s/def ::function (s/and (function :spec)
                         (hs/entity-keys :req [::package/_member])))
