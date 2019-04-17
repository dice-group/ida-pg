(ns librarian.generator.query
  (:require [librarian.model.concepts.datatype :as datatype]
            [librarian.model.concepts.typed :as typed]))

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
                   (not [?from ::typed/datatype ?from-type]
                        (subdatatype ?to-type ?from-type)))]])
