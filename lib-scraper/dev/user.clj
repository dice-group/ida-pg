(ns user
  "REPL startup namespace that contains various helper methods to interact with the application."
  (:require [clojure.tools.namespace.repl :as ctnr]
            [clojure.stacktrace :refer :all]
            [proto-repl.saved-values]))

(defn start
  []
  (require '[lib-scraper.core :refer :all])
  (set! *warn-on-reflection* true)
  (println "REPL started."))

(defn refresh
  []
  (ctnr/refresh :after 'user/start))

(defn refresh-all
  []
  (ctnr/refresh-all :after 'user/start))
