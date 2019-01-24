(ns lib-scraper.model.concepts.namespaced
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.helpers.transaction :refer [add-attr]]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.model.concepts.named :as named :refer [named]]
            [lib-scraper.model.concepts.namespace :as namespace]))

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
