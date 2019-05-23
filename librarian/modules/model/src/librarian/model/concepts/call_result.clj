(ns librarian.model.concepts.call-result
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.typed :refer [typed]]
            [librarian.model.concepts.data-receiver :refer [data-receiver]]))

(defconcept call-result [typed data-receiver]
  :attributes {::result {:db/valueType :db.type/ref
                         :db/doc "A callable's result."}}
  :spec ::call-result)

(s/def ::call-result (hs/entity-keys :opt [::result]))
(s/def ::result (hs/instance? :librarian.model.concepts.result/result))
