(ns librarian.model.concepts.call-parameter
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.typed :refer [typed]]
            [librarian.model.concepts.data-receiver :refer [data-receiver]]))

(defconcept call-parameter [typed data-receiver]
  :attributes {::parameter {:db/valueType :db.type/ref
                            :db/doc "A callable's parameter."}}
  :spec ::call-parameter)

(s/def ::call-parameter (hs/entity-keys :opt [::parameter ::receives]))
(s/def ::parameter (hs/instance? :librarian.model.concepts.parameter/parameter))
