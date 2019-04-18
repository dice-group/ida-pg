(ns librarian.model.concepts.result
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.io-container :refer [io-container]]
            [librarian.model.concepts.callable :as callable]))

(defconcept result [io-container]
  :attributes {::receives {:db/valueType :db.type/ref
                           :db/index true
                           :db/doc "An io-container whose value this result mirrors."}
               ::receives-semantic {:db/valueType :db.type/ref
                                    :db/cardinality :db.cardinality/many
                                    :db/index true
                                    :db/doc "io-containers whose semantic types propagate to this result."}}
  :spec ::result)

(s/def ::result (hs/entity-keys :req [::callable/_result]
                                :opt [::receives ::receives-semantic]))
(s/def ::receives (hs/instance? io-container))
(s/def ::receives-semantic (s/coll-of (hs/instance? io-container)))
