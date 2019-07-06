(ns librarian.model.paradigms.oo.constructor
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.paradigms.oo.class :as class]
            [librarian.model.concepts.callable :refer [callable]]))

(defconcept constructor [callable]
  :spec ::constructor)

(s/def ::constructor (hs/entity-keys :req [::class/_constructor]))
