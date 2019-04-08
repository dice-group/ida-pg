(ns librarian.model.ecosystems.python.constructor
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.paradigms.oo.constructor :refer [constructor]
             :rename {constructor oo-constructor}]))

(defconcept constructor [oo-constructor]
  :attributes {::class {:db/unique :db.unique/identity
                        :db/valueType :db.type/ref
                        :librarian/internal true
                        :librarian/computed true
                        :db/doc (str "A reference to the constructor's class. "
                                     "In python this uniquely identifies a constructor.")}}
  :spec ::constructor)

(s/def ::constructor (hs/entity-keys :req [::class]))
(s/def ::class (hs/instance? :librarian.model.ecosystems.python.class/class))
