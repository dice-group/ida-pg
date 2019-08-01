(ns librarian.model.concepts.call
  (:require [clojure.spec.alpha :as s]
            [datascript.core :as d]
            [librarian.helpers.spec :as hs]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.typed :refer [typed]]
            [librarian.model.concepts.callable :as callable]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-result :as call-result]))

(defn postprocess
  [db id]
  (let [e (d/entity db id)
        callable (::callable e)]
    (when (:placeholder callable)
      (let [params (::parameter e)
            results (::result e)
            cid (:db/id callable)
            tx (-> []
                   (into (comp (keep ::call-parameter/parameter)
                               (map :db/id)
                               (map (fn [id] [:db/add cid ::callable/parameter id])))
                         params)
                   (into (comp (keep ::call-result/result)
                               (map :db/id)
                               (map (fn [id] [:db/add cid ::callable/result id])))
                         results))]
        tx))))

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
  :spec ::call
  :postprocess postprocess)

(s/def ::call (hs/entity-keys :opt [::callable ::parameter ::result]))
(s/def ::callable (hs/instance? ::callable/callable))
(s/def ::parameter (s/coll-of (hs/instance? ::call-parameter/call-parameter)))
(s/def ::result (s/coll-of (hs/instance? ::call-result/call-result)))
