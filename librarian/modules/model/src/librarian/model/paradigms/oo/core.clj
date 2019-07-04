(ns librarian.model.paradigms.oo.core
  (:require [librarian.model.syntax :refer [defparadigm]]
            [librarian.model.paradigms.common :refer [common]]
            [librarian.model.paradigms.oo.class :refer [class]]
            [librarian.model.paradigms.oo.constructor :refer [constructor]]
            [librarian.model.paradigms.oo.method :refer [method]])
  (:refer-clojure :exclude [class]))

(defparadigm oo [common]
  :concepts
  {:class class
   :constructor constructor
   :method method})
