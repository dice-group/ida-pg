(defproject librarian/librarian "1.0.0-SNAPSHOT"
  :description "A set of tools to programmatically work with software libraries."
  :url "https://github.com/dice-group/ida"

  :plugins [[lein-modules "0.3.11"]]
  :middleware [lein-modules.plugin/middleware]

  :dependencies [[org.clojure/clojure]
                 [cli-matic "0.3.6"]
                 [say-cheez "0.1.1"]
                 [librarian/helpers]
                 [librarian/model]
                 [librarian/scraper]
                 [librarian/generator]]

  :modules {:versions {org.clojure/clojure "1.10.1"
                       org.clojure/tools.logging "0.4.1"
                       datascript "0.18.3"
                       clj-commons/fs "1.5.0"
                       org.flatland/ordered "1.5.7"

                       librarian/helpers "1.0.0-SNAPSHOT"
                       librarian/model "1.0.0-SNAPSHOT"
                       librarian/scraper "1.0.0-SNAPSHOT"
                       librarian/generator "1.0.0-SNAPSHOT"}

            :dirs ["."
                   "modules/helpers"
                   "modules/model"
                   "modules/scraper"
                   "modules/generator"]

            :inherited {:pom-plugins [[com.theoryinpractise/clojure-maven-plugin "1.8.1"
                                       [:extensions true
                                        :configuration ([:sourceDirectories [:sourceDirectory "src"]]
                                                        [:testSourceDirectories [:testSourceDirectory "test"]])
                                        :executions [:execution ([:goals [:goal "compile"]]
                                                                 [:phase "compile"])]]]]}}

  :main librarian.cli

  :aliases {"prepare-repl" ["do" ["modules" "clean"]
                                 ["modules" "install"]
                                 ["modules" "clean"]
                                 ["modules" ":checkouts"]]}

  :profiles {:dev {:source-paths ["dev" "src" "test"]
                   :dependencies [[org.clojure/tools.namespace "0.3.0-alpha4"]
                                  [proto-repl "0.3.1"]
                                  [proto-repl-charts "0.3.2"]
                                  [proto-repl-sayid "0.1.3"]
                                  [com.gfredericks/debug-repl "0.0.10"]]
                   :eastwood {:exclude-linters []}
                   :repl-options {:init-ns user
                                  :init (start)
                                  :nrepl-middleware
                                  [com.gfredericks.debug-repl/wrap-debug-repl]}}
             :uberjar {:aot :all
                       :uberjar-name "librarian.jar"}})
