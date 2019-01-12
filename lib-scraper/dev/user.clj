(ns user
  "REPL startup namespace that contains various helper methods to interact with the application."
  (:require [clojure.tools.namespace.repl :as ctnr]
            [clojure.tools.logging :as log]
            [clojure.stacktrace :refer :all]
            [proto-repl.saved-values]))

(defn start
  []
  (require '[lib-scraper.io.core :refer :all]
           '[lib-scraper.core :refer [main*]])
  (log/info "REPL started."))

(defn refresh
  []
  (ctnr/refresh :after 'user/start))

(defn refresh-all
  []
  (ctnr/refresh-all :after 'user/start))
