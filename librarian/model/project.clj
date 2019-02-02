(defproject librarian/model "1.0.0-SNAPSHOT"
  :description "A library for abstract language ecosystem models."
  :url "https://github.com/dice-group/ida"

  :plugins [[lein-modules "0.3.11"]]
  :middleware [lein-modules.plugin/middleware]

  :dependencies [[org.clojure/clojure "_"]
                 [org.clojure/tools.logging "_"]
                 [datascript "_"]
                 [clj-commons/fs "_"]
                 [io.forward/semver "0.1.0"]
                 [librarian/helpers "_"]])
