(ns librarian.model.concepts.io-container
  (:require [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.named :refer [named]]
            [librarian.model.concepts.typed :refer [typed]]
            [librarian.model.concepts.positionable :refer [positionable]]))

(defconcept io-container [named typed positionable])
