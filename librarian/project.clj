(defproject librarian/librarian "1.0.0-SNAPSHOT"
  :description "A set of tools to programmatically work with software libraries."
  :url "https://github.com/dice-group/ida"

  :plugins [[lein-modules "0.3.11"]]
  :middleware [lein-modules.plugin/middleware]

  :modules {:versions {org.clojure/clojure "1.10.0"
                       org.clojure/tools.logging "0.4.1"
                       datascript "0.17.1"
                       clj-commons/fs "1.5.0"

                       librarian/helpers "1.0.0-SNAPSHOT"
                       librarian/model "1.0.0-SNAPSHOT"
                       librarian/scraper "1.0.0-SNAPSHOT"}})
