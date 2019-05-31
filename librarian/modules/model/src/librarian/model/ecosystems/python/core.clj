(ns librarian.model.ecosystems.python.core
  (:require [librarian.model.syntax :refer [defecosystem]]
            [librarian.model.paradigms.oo.core :refer [oo]]
            [librarian.model.paradigms.functional.core :refer [functional]]
            [librarian.model.ecosystems.python.class :refer [class]]
            [librarian.model.ecosystems.python.constructor :refer [constructor]]
            [librarian.model.ecosystems.python.basetype :refer [basetype]]
            [librarian.model.ecosystems.python.builtins :refer [builtins]])
  (:refer-clojure :exclude [class]))

(defecosystem python [oo functional]
  :concepts
  {:class class
   :constructor constructor
   :basetype basetype}

  :builtins builtins)
