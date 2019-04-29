(defproject librarian/helpers "1.0.0-SNAPSHOT"
  :description "A collection of general purpose helper functions."

  :plugins [[lein-modules "0.3.11"]]
  :middleware [lein-modules.plugin/middleware]

  :dependencies [[org.clojure/clojure]
                 [org.clojure/tools.logging]
                 [datascript]])
