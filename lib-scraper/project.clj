(defproject upb/lib-scraper "0.0.1-SNAPSHOT"
  :description "Crawls through the documentation of software libraries and transforms them into a standardized format."
  :url "https://github.com/dice-group/ida"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.logging "0.4.1"]
                 [edu.uci.ics/crawler4j "4.4.0"]
                 [org.flatland/ordered "1.5.7"]
                 [hickory "0.7.1"]
                 [datascript "0.17.1"]
                 [clj-commons/fs "1.5.0"]
                 [say-cheez "0.1.1"]
                 [cli-matic "0.3.3"]
                 [io.forward/semver "0.1.0"]]
  :repositories [["oracleReleases" {:url "https://download.oracle.com/maven"}]]

  :plugins [[lein-cljfmt "0.6.2"]]

  :aot :all

  :profiles {:dev {:source-paths ["dev" "src" "test"]
                   :aot ^:replace [lib-scraper.crawler.factory]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [proto-repl "0.3.1"]
                                  [proto-repl-charts "0.3.1"]
                                  [proto-repl-sayid "0.1.3"]]
                   :repl-options {:init-ns user
                                  :init (start)}
                   :eastwood {:exclude-linters []}}
             :scraper {:main lib-scraper.core
                       :uberjar-name "lib-scraper.jar"}}

  :aliases {"build-scraper" ["with-profile" "scraper" "uberjar"]}

  :pom-plugins [[com.theoryinpractise/clojure-maven-plugin "1.8.1"
                 [:extensions true
                  :configuration ([:sourceDirectories [:sourceDirectory "src"]]
                                  [:testSourceDirectories [:testSourceDirectory "test"]])
                  :executions [:execution ([:goals [:goal "compile"]]
                                           [:phase "compile"])]]]])
