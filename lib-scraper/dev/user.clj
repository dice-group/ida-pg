(ns user
  "REPL startup namespace that contains various helper methods to interact with the application."
  (:require [clojure.tools.namespace.repl :as ctnr]
            [clojure.tools.logging :as log]
            [clojure.stacktrace :refer :all]
            [clojure.string :as string]
            [proto-repl.saved-values]))

(defn start
  []
  (require '[lib-scraper.io.core :refer :all]
           '[lib-scraper.core :refer [main*]]
           '[repl-tools :refer :all])

  (log/info "REPL started.")
  (letfn [(lines [& args] (string/join "\n" args))]
    (println (lines "Useful commands:"
                    "* (create-scrape config-file)"
                    "* (show-scrape config-file)"
                    "* (query-scrape scrape query & args)"
                    "* (query-file scrape-file query & args)"
                    "* (pull-scrape scrape selector eid)"
                    "* (pull-file scrape-file selector eid)"
                    "* (main* & args)"))))

(defn refresh
  []
  (ctnr/refresh :after 'user/start))

(defn refresh-all
  []
  (ctnr/refresh-all :after 'user/start))
