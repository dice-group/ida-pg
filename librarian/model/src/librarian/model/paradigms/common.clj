(ns librarian.model.paradigms.common
  (:require [librarian.model.syntax :refer [defparadigm]]
            [librarian.model.concepts.named :refer [named]]
            [librarian.model.concepts.callable :refer [callable]]
            [librarian.model.concepts.parameter :refer [parameter]]
            [librarian.model.concepts.datatype :refer [datatype]]
            [librarian.model.concepts.namespace :refer [namespace]]
            [librarian.model.concepts.namespaced :refer [namespaced]])
  (:refer-clojure :exclude [namespace]))

(defparadigm common
  :named named
  :callable callable
  :parameter parameter
  :datatype datatype
  :namespace namespace
  :namespaced namespaced)
