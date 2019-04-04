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
   #_(println (fix ecosystem tx-data))
   (d/with db
           (fix ecosystem tx-data)
           tx-meta)))
