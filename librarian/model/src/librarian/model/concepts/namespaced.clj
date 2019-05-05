(ns librarian.model.concepts.namespaced
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.helpers.transaction :refer [add-attr]]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.named :as named :refer [named]]
            [librarian.model.concepts.namespace :as namespace]))

(defn fqn
  "Computes the fully qualified name of a given concept."
  [e]
  (when-let [pname (some-> e ::namespace/_member ::named/name)]
    (str pname "." (::named/name e))))

(defconcept namespaced [named]
  :attributes {::id {:db/unique :db.unique/identity
                     :db/doc "Fully qualified name of the concept."}}
  :spec ::namespaced
  :postprocess (add-attr ::id fqn))

(s/def ::namespaced (hs/entity-keys :req [::id ::namespace/_member]))
