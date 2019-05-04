(ns librarian.model.concepts.datatype
  (:require [librarian.helpers.transaction :refer [add-attr]]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.named :as named :refer [named]]))

(defconcept datatype [named]
  :attributes {::id {:db/unique :db.unique/identity
                     :db/doc "Unique name of the datatype."}}
  :preprocess {::named/name (add-attr ::id)})
