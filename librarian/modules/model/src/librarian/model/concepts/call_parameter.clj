(ns librarian.model.concepts.call-parameter
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.helpers.predicate :refer [p-or]]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.typed :as typed :refer [typed]]))

(defconcept call-parameter [typed]
  :attributes {::parameter {:db/valueType :db.type/ref
                            :db/doc "A callable's parameter."}
               ::receives {:db/valueType :db.type/ref
                           :db/doc "A call-result that flows into this call parameter."}}
  :spec ::call-parameter)

(s/def ::call-parameter (hs/entity-keys :opt [::parameter ::receives]))
(s/def ::parameter (hs/instance? :librarian.model.concepts.parameter/parameter))
(s/def ::receives (p-or (hs/instance? :librarian.model.concepts.call-result/call-result)
                        (hs/instance? :librarian.model.concepts.call-value/call-value)))
