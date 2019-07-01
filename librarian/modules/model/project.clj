(defproject librarian/model "1.0.0-SNAPSHOT"
  :description "A library for abstract language ecosystem models."

  :plugins [[lein-modules "0.3.11"]]
  :middleware [lein-modules.plugin/middleware]

  :dependencies [[org.clojure/clojure]
                 [org.clojure/tools.logging]
                 [datascript]
                 [clj-commons/fs]
                 [aysylu/loom]
                 [org.clojure/data.json "0.2.6"]
                 [io.forward/semver "0.1.0"]
                 [cnuernber/libpython-clj "0.11"]
                 [librarian/helpers]]

  :modules {:parent "../.."})
