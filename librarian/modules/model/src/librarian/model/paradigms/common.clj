(ns librarian.model.paradigms.common
  "Definition of a universal paradigm containing commonly used concepts found in most languages."
  (:require [librarian.model.syntax :refer [defparadigm]]
            [librarian.model.concepts.named :refer [named]]
            [librarian.model.concepts.namespace :refer [namespace]]
            [librarian.model.concepts.namespaced :refer [namespaced]]
            [librarian.model.concepts.datatype :refer [datatype]]
            [librarian.model.concepts.basetype :refer [basetype]]
            [librarian.model.concepts.semantic-type :refer [semantic-type]]
            [librarian.model.concepts.role-type :refer [role-type]]
            [librarian.model.concepts.typed :refer [typed]]
            [librarian.model.concepts.data-receiver :refer [data-receiver]]
            [librarian.model.concepts.callable :refer [callable]]
            [librarian.model.concepts.io-container :refer [io-container]]
            [librarian.model.concepts.parameter :refer [parameter]]
            [librarian.model.concepts.result :refer [result]]
            [librarian.model.concepts.call :refer [call]]
            [librarian.model.concepts.call-parameter :refer [call-parameter]]
            [librarian.model.concepts.call-result :refer [call-result]]
            [librarian.model.concepts.constant :refer [constant]]
            [librarian.model.concepts.snippet :refer [snippet]])
  (:refer-clojure :exclude [namespace]))

(defparadigm common
  :concepts
  {:named named
   :namespace namespace
   :namespaced namespaced

   :datatype datatype
   :basetype basetype
   :semantic-type semantic-type
   :role-type role-type
   :typed typed
   :data-receiver data-receiver

   :callable callable
   :io-container io-container
   :parameter parameter
   :result result

   :call call
   :call-parameter call-parameter
   :call-result call-result
   :constant constant
   :snippet snippet})
