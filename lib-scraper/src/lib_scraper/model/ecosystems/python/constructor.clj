(ns lib-scraper.model.ecosystems.python.constructor
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.model.paradigms.oo.constructor :refer [constructor]
             :rename {constructor oo-constructor}]))

(defconcept constructor [oo-constructor]
  :attributes {::class {:db/unique :db.unique/identity
                        :db/valueType :db.type/ref
                        :lib-scraper/internal true
                        :db/doc (str "A reference to the constructor's class. "
                                     "In python this uniquely identifies a constructor.")}}
  :spec ::constructor)

(s/def ::constructor (hs/entity-keys :req [::class]))
