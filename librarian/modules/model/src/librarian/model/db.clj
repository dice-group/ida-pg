(ns librarian.model.db
  (:require [datascript.core :as d]
            [clojure.walk :as walk]))

(defn fix
  [{:keys [concept-aliases attribute-aliases]} form]
  (walk/prewalk-replace (merge concept-aliases
                               attribute-aliases)
                        form))

(defn q
  "Like datascript.core/q but uses an ecosystem to resolve concept and attribute aliases in queries."
  [ecosystem query & inputs]
  (apply d/q (fix ecosystem query) inputs))

(defn pull
  "Like datascript.core/pull but uses an ecosystem to resolve concept and attribute aliases in queries."
  [ecosystem db selector eid]
  (d/pull db
          (fix ecosystem selector)
          (fix ecosystem eid)))

(defn with
  "Like datascript.core/with but uses an ecosystem to resolve concent and attribute aliases in queries."
  ([ecosystem db tx-data]
   (with ecosystem db tx-data nil))
  ([ecosystem db tx-data tx-meta]
   (d/with db (fix ecosystem tx-data) tx-meta)))

(defn with-seq
  "Performs the transaction sequence on a given database."
  [db tx-seq]
  (reduce (fn [db instances]
            (if instances
              (d/db-with db instances)
              db))
          db tx-seq))

(defn transact-seq!
  "Performs the transaction sequence on a given database connection."
  [conn tx-seq]
  (doseq [instances tx-seq
          :when (some? instances)]
    (d/transact! conn instances)))

(def rules
  [; is ?c a concept of type ?type:
   '[(type ?c ?type)
     [?c :type ?type]]
   '[(type ?c ?type)
     [?c :type ?t]
     (subtype ?t ?type)]

   ; is ?parent a superconcept of ?child:
   '[(subtype ?child ?parent)
     [(clojure.core/isa? ?child ?parent)]]])
