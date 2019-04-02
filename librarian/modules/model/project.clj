(defproject librarian/model "1.0.0-SNAPSHOT"
  :description "A library for abstract language ecosystem models."

  :plugins [[lein-modules "0.3.11"]]
  :middleware [lein-modules.plugin/middleware]

  :dependencies [[org.clojure/clojure]
                 [org.clojure/tools.logging]
                 [datascript]
                 [clj-commons/fs]
                 [io.forward/semver "0.1.0"]
                 [librarian/helpers]]

  :modules {:parent "../.."})
