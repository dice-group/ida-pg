(defproject librarian/helpers "1.0.0-SNAPSHOT"
  :description "A collection of general purpose helper functions."
  :url "https://github.com/dice-group/ida"

  :plugins [[lein-modules "0.3.11"]]
  :middleware [lein-modules.plugin/middleware]

  :dependencies [[org.clojure/clojure "_"]
                 [org.clojure/tools.logging "_"]
                 [datascript "_"]])
