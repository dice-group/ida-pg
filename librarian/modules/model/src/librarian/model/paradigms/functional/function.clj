(ns librarian.model.paradigms.functional.function
  (:require [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.namespaced :refer [namespaced]]
            [librarian.model.concepts.callable :refer [callable]]))

(defconcept function [namespaced callable])
