(defproject librarian/librarian "1.0.0-SNAPSHOT"
  :description "A set of tools to programmatically work with software libraries."
  :url "https://github.com/dice-group/ida"

  :plugins [[lein-modules "0.3.11"]]
  :middleware [lein-modules.plugin/middleware]
  :dependencies [[org.clojure/clojure]]

  :modules {:versions {org.clojure/clojure "1.10.0"
                       org.clojure/tools.logging "0.4.1"
                       datascript "0.17.1"
                       clj-commons/fs "1.5.0"

                       librarian/helpers "1.0.0-SNAPSHOT"
                       librarian/model "1.0.0-SNAPSHOT"
                       librarian/scraper "1.0.0-SNAPSHOT"}

            :inherited {:pom-plugins [[com.theoryinpractise/clojure-maven-plugin "1.8.1"
                                       [:extensions true
                                        :configuration ([:sourceDirectories [:sourceDirectory "src"]]
                                                        [:testSourceDirectories [:testSourceDirectory "test"]])
                                        :executions [:execution ([:goals [:goal "compile"]]
                                                                 [:phase "compile"])]]]]}}

  :aliases {"pom" ["modules" "pom"]} ; lein pom should only update the submodule poms

  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [proto-repl "0.3.1"]
                                  [proto-repl-charts "0.3.1"]
                                  [proto-repl-sayid "0.1.3"]]
                   :eastwood {:exclude-linters []}}})
