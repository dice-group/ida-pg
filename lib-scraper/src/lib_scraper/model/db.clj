(ns lib-scraper.model.db
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
  (apply d/q (substitute-aliases ecosystem query) inputs))

(defn pull
  "Like datascript.core/pull but uses an ecosystem to resolve concept and attribute aliases in queries."
  [ecosystem db selector eid]
  (d/pull db
          (substitute-aliases ecosystem selector)
          (substitute-aliases ecosystem eid)))
