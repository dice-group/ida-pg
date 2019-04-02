(ns librarian.model.concepts.callable
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]))

(defconcept callable
  :attributes {::parameter {:db/valueType :db.type/ref
                            :db/cardinality :db.cardinality/many
                            :db/isComponent true
                            :db/doc "A parameter of the callable."}
               ::result {:db/valueType :db.type/ref
                         :db/cardinality :db.cardinality/many
                         :db/isComponent true
                         :db/doc "A result (i.e. return value) of the callable."}}
  :spec ::callable)

(s/def ::callable (hs/entity-keys :opt [::parameter ::result]))
(s/def ::parameter (s/coll-of (hs/instance? :librarian.model.concepts.parameter/parameter)))
(s/def ::result (s/coll-of (hs/instance? :librarian.model.concepts.result/result)))
