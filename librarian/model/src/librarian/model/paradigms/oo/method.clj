(ns librarian.model.paradigms.oo.method
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.named :refer [named]]
            [librarian.model.concepts.callable :refer [callable]]
            [librarian.model.paradigms.oo.class :as class]))

(defconcept method [named callable]
  :spec ::method)

(s/def ::method (hs/entity-keys :req [::class/_method]))
