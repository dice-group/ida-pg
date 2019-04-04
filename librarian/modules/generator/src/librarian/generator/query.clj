(ns librarian.generator.query
  (:require [librarian.model.concepts.datatype :as datatype]))

(def rules ['[(type ?c ?type)
              [?c :type ?t]
              (subtype ?type ?t)]

            `[~'(subtype ?parent ?child)
              [(isa? ~'?child ~'?parent)]]

            '[(subdatatype ?parent ?child)
              (or [(= ?parent ?child)]
                  [?child ::datatype/extends ?parent])]])
