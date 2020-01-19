(ns librarian.model.paradigms.functional.core
  "Definition of the functional programming paradigm."
  (:require [librarian.model.syntax :refer [defparadigm]]
            [librarian.model.paradigms.common :refer [common]]
            [librarian.model.paradigms.functional.function :refer [function]]))

(defparadigm functional [common]
  :concepts
  {:function function})
