(ns user
  "REPL startup namespace that contains various helper methods to interact with the application."
  (:require [clojure.tools.namespace.repl :as ctnr]
            [clojure.tools.logging :as log]
            [clojure.stacktrace :refer :all]
            [clojure.repl :refer :all :exclude [root-cause]]
            [clojure.pprint :refer :all]
            [clojure.string :as string]
            [proto-repl.saved-values]))

(defn start
  []
  (ctnr/set-refresh-dirs "dev" "src" "test"
                         "modules/helpers/src"
                         "modules/model/src"
                         "modules/scraper/src"
                         "modules/generator/src")

  (require '[librarian.model.io.scrape :refer :all]
           '[librarian.scraper.io.scrape :refer [create-scrape]]
           '[librarian.scraper.io.config :refer [read-config]]
           '[librarian.cli :refer [main*]]
           '[repl-tools :as rt :refer :all]
           '[generator :refer :all])

  (log/info "REPL started.")
  (letfn [(lines [& args] (string/join "\n" args))]
    (println (lines "CLI command: (main* & args)"
                    ""
                    "Scraper commands:"
                    "* (create-scrape config-file)"
                    "* (show-scrape config-file)"
                    "* (query-scrape scrape query & args)"
                    "* (query-file scrape-file query & args)"
                    "* (pull-scrape scrape selector eid)"
                    "* (pull-file scrape-file selector eid)"
                    ""
                    "Generator commands:"
                    " * (gen-test [scrape-file])"
                    ""
                    "REPL commands:"
                    " * (refresh)"
                    " * (refresh-all)"
                    " * all of clojure.repl: doc, source, apropos, pst, dir, ..."))))

(defn refresh
  []
  (ctnr/refresh :after 'user/start))

(defn refresh-all
  []
  (ctnr/refresh-all :after 'user/start))
