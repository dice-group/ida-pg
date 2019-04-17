(ns librarian.generator.query
  (:require [librarian.model.concepts.datatype :as datatype]
            [librarian.model.concepts.typed :as typed]
            [librarian.model.concepts.semantic-type :as semantic-type]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-result :as call-result]))

(def rules [; is ?c a concept of type ?type:
            '[(type ?c ?type)
              [?c :type ?t]
              (subtype ?type ?t)]

            ; is ?parent a superconcept of ?child:
            '[(subtype ?parent ?child)
              [(clojure.core/isa? ?child ?parent)]]

            ; does the datatype concept ?child extend the datatype concept ?parent:
            '[(subdatatype ?parent ?child)
              [(= ?parent ?child)]]
            '[(subdatatype ?parent ?child)
              [?child ::datatype/extends ?p]
              (subdatatype ?parent ?p)]

            ; can values of typed concept ?from be used as values of typed concept ?to:
            '[(typed-compatible ?from ?to)
              (not [?to ::typed/datatype ?to-type]
                   (not (type ?to-type ::semantic-type/semantic-type))
                   (not [?from ::typed/datatype ?from-type]
                        (subdatatype ?to-type ?from-type)))]

            ; does ?a depend on ?b:
            '[(depends-on ?a ?b)
              [(= ?a ?b)]]
            '[(depends-on ?a ?b)
              (type ?a ::call/call)
              [?a ::call/parameter ?param]
              (depends-on ?param ?b)]
            '[(depends-on ?a ?b)
              (type ?a ::call-parameter/call-parameter)
              [?a ::call-parameter/receives ?val]
              (depends-on ?val ?b)]
            '[(depends-on ?a ?b)
              (type ?a ::call-result/call-result)
              [?call ::call/result ?a]
              (depends-on ?call ?b)]])
