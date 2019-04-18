(ns librarian.model.concepts.namespace
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.helpers.transaction :refer [add-attr]]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.named :as named :refer [named]])
  (:refer-clojure :exclude [namespace]))

(defconcept namespace [named]
  :attributes {::id {:db/unique :db.unique/identity
                     :librarian/computed true
                     :db/doc "Unique name of the namespace."}
               ::member {:db/valueType :db.type/ref
                         :db/cardinality :db.cardinality/many
                         :db/isComponent true
                         :db/index true
                         :db/doc "Concept is member of the namespace."}}
  :preprocess {::named/name (add-attr ::id)})

(s/def ::namespace (hs/entity-keys :opt [::member]))
(s/def ::member (s/coll-of (hs/instance? :librarian.model.concepts.namespaced/namespaced)))
