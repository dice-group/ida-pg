(ns librarian.model.concepts.namespace
  (:require [librarian.helpers.transaction :refer [add-attr]]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.named :as named :refer [named]])
  (:refer-clojure :exclude [namespace]))

(defconcept namespace [named]
  :attributes {::id {:db/unique :db.unique/identity
                     :db/doc "Unique name of the namespace."}
               ::member {:db/valueType :db.type/ref
                         :db/cardinality :db.cardinality/many
                         :db/isComponent true
                         :db/doc "Concept is member of the namespace."}}
  :preprocess {::named/name (add-attr ::id)})
