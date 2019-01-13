(ns lib-scraper.model.db
  (:require [datascript.core :as d]
            [clojure.walk :as walk]))

(defn q
  "Like datascript.core/q but uses an ecosystem to resolve concept and attribute aliases in queries."
  [{:keys [concept-aliases attribute-aliases]} query & inputs]
  (let [query (walk/prewalk-replace (merge concept-aliases
                                           attribute-aliases)
                                    query)]
    (apply d/q query inputs)))
