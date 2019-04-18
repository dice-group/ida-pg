(ns librarian.model.concepts.call
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.typed :refer [typed]]))

(defconcept call [typed]
  :attributes {::callable {:db/valueType :db.type/ref
                           :db/doc "The callable of this call."}
               ::parameter {:db/valueType :db.type/ref
                            :db/cardinality :db.cardinality/many
                            :db/isComponent true
                            :db/index true
                            :db/doc "A parameter of this call."}
               ::result {:db/valueType :db.type/ref
                         :db/cardinality :db.cardinality/many
                         :db/isComponent true
                         :db/index true
                         :db/doc "A result of this call."}}
  :spec ::call)

(s/def ::call (hs/entity-keys :opt [::callable ::parameter ::result]))
(s/def ::callable (hs/instance? :librarian.model.concepts.callable/callable))
(s/def ::parameter (hs/instance? :librarian.model.concepts.call-parameter/call-parameter))
(s/def ::result (hs/instance? :librarian.model.concepts.call-result/call-result))
