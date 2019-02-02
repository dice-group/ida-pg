(defproject librarian/scraper "1.0.0-SNAPSHOT"
  :description "Crawls through the documentation of software libraries and transforms them into a standardized format."
  :url "https://github.com/dice-group/ida"

  :plugins [[lein-modules "0.3.11"]]
  :middleware [lein-modules.plugin/middleware]

  :dependencies [[org.clojure/clojure "_"]
                 [org.clojure/tools.logging "_"]
                 [edu.uci.ics/crawler4j "4.4.0"]
                 [org.flatland/ordered "1.5.7"]
                 [hickory "0.7.1"]
                 [datascript "_"]
                 [clj-commons/fs "_"]
                 [say-cheez "0.1.1"]
                 [cli-matic "0.3.3"]
                 [librarian/helpers "_"]
                 [librarian/model "_"]]
  :repositories [["oracleReleases" {:url "https://download.oracle.com/maven"}]]

  :aot [librarian.scraper.crawler.factory]

  :profiles {:dev {:source-paths ["dev" "src" "test"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [proto-repl "0.3.1"]
                                  [proto-repl-charts "0.3.1"]
                                  [proto-repl-sayid "0.1.3"]]
                   :repl-options {:init-ns user
                                  :init (start)}
                   :eastwood {:exclude-linters []}}
             :scraper {:main librarian.scraper.cli
                       :aot :all
                       :uberjar-name "librarian-scraper.jar"}
             :generator {:aot :all}}

  :aliases {"build-scraper" ["with-profile" "scraper" "uberjar"]}

  :pom-plugins [[com.theoryinpractise/clojure-maven-plugin "1.8.1"
                 [:extensions true
                  :configuration ([:sourceDirectories [:sourceDirectory "src"]]
                                  [:testSourceDirectories [:testSourceDirectory "test"]])
                  :executions [:execution ([:goals [:goal "compile"]]
                                           [:phase "compile"])]]]])
