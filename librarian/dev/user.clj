(ns user
  "REPL startup namespace that contains various helper methods to interact with the application."
  (:require [clojure.tools.namespace.repl :as ctnr]
            [clojure.tools.logging :as log]
            [clojure.stacktrace :refer :all]
            [clojure.string :as string]
            [proto-repl.saved-values]))

(defn start
  []
  (ctnr/set-refresh-dirs "dev" "src" "test"
                         "modules/helpers/src"
                         "modules/model/src"
                         "modules/scraper/src"
                         "modules/generator/src")

  (require '[librarian.model.io.scrape :as scrape :refer :all]
           '[librarian.scraper.io.scrape :refer [create-scrape]]
           '[librarian.scraper.io.config :refer [read-config]]
           '[librarian.generator.core :as gen]
           '[librarian.cli :refer [main*]]
           '[repl-tools :as rt :refer :all])

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
                    "[WIP]"
                    ""
                    "REPL commands:"
                    " * (refresh)"
                    " * (refresh-all)"))))

(defn refresh
  []
  (ctnr/refresh :after 'user/start))

(defn refresh-all
  []
  (ctnr/refresh-all :after 'user/start))

(defn gen-test
  ([] (gen-test "libs/scikit-learn-cluster"))
  ([ds]
   (let [scrape (scrape/read-scrape ds)
         search-state (gen/initial-search-state scrape [:dataset] [:labels])
         succs (iterate gen/continue-search search-state)
         succs (take 10 succs)]
     (time (rt/show-search-state (or (some #(when (:goal %) %) succs)
                                     (last succs)
                                  :show-patterns false))))))
