(ns librarian.model.db
  (:require [datascript.core :as d]
            [clojure.walk :as walk]
            [librarian.model.syntax :refer [instances->tx]]))

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

(defn add-builtins
  "Adds the ecosystem builtin concepts to a given database."
  [db ecosystem]
  (reduce (fn [db instances]
            (when instances
              (-> db (d/with (instances->tx instances)) :db-after)
              db))
          db (:builtins ecosystem)))

(defn transact-builtins!
  "Transacts the ecosystem builtin concepts to a given database connection."
  [conn ecosystem]
  (doseq [instances (:builtins ecosystem)]
    (d/transact! conn (instances->tx instances))))
