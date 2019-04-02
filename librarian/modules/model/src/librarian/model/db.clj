(ns librarian.model.db
  (:require [datascript.core :as d]
            [clojure.walk :as walk]))

(defn substitute-aliases
  [{:keys [concept-aliases attribute-aliases]} form]
  (walk/prewalk-replace (merge concept-aliases
                               attribute-aliases)
                        form))

(defn q
  "Like datascript.core/q but uses an ecosystem to resolve concept and attribute aliases in queries."
  [ecosystem query & inputs]
  ; (println (substitute-aliases ecosystem query))
  (apply d/q (substitute-aliases ecosystem query) inputs))

(defn pull
  "Like datascript.core/pull but uses an ecosystem to resolve concept and attribute aliases in queries."
  [ecosystem db selector eid]
  (d/pull db
          (substitute-aliases ecosystem selector)
          (substitute-aliases ecosystem eid)))

(defn with
  "Like datascript.core/with but uses an ecosystem to resolve concent and attribute aliases in queries."
  ([ecosystem db tx-data]
   (with ecosystem db tx-data nil))
  ([ecosystem db tx-data tx-meta]
   (d/with db
           (substitute-aliases ecosystem tx-data)
           tx-meta)))
