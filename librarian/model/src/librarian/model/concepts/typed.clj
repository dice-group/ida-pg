(ns librarian.model.concepts.typed
  (:require [librarian.model.syntax :refer [defconcept]]))

(defconcept typed
  :attributes {::datatype {:db/valueType :db.type/ref
                           :db/doc "Datatype of this concept."}})
