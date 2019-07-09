(ns librarian.model.concepts.data-receiver
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.data-receivable :as data-receivable :refer [data-receivable]]))

(defconcept data-receiver [data-receivable]
  :attributes {::receives {:db/valueType :db.type/ref
                           :db/index true
                           :db/doc "A data-receiver whose value this receiver mirrors."}
               ::receives-semantic {:db/valueType :db.type/ref
                                    :db/cardinality :db.cardinality/many
                                    :db/index true
                                    :db/doc "data-receivers whose semantic types propagate to this receiver."}}
  :spec ::data-receiver)

(s/def ::data-receiver (hs/entity-keys :opt [::receives ::receives-semantic]))
(s/def ::receives (hs/instance? ::data-receivable/data-receivable))
(s/def ::receives-semantic (s/coll-of (hs/instance? ::data-receivable/data-receivable)))
