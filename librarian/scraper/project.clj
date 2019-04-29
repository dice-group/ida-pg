(defproject librarian/scraper "1.0.0-SNAPSHOT"
  :description "Crawls through the documentation of software libraries and transforms them into a standardized format."

  :plugins [[lein-modules "0.3.11"]]
  :middleware [lein-modules.plugin/middleware]

  :dependencies [[org.clojure/clojure]
                 [org.clojure/tools.logging]
                 [edu.uci.ics/crawler4j "4.4.0"]
                 [org.flatland/ordered "1.5.7"]
                 [hickory "0.7.1"]
                 [datascript]
                 [clj-commons/fs]
                 [say-cheez "0.1.1"]
                 [cli-matic "0.3.3"]
                 [librarian/helpers]
                 [librarian/model]]
  :repositories [["oracleReleases" {:url "https://download.oracle.com/maven"}]]

  :aot [librarian.scraper.crawler.factory]

  :profiles {:dev {:source-paths ["dev" "src" "test"]
                   :repl-options {:init-ns user
                                  :init (start)}}
             :scraper {:main librarian.scraper.cli
                       :aot :all
                       :uberjar-name "librarian-scraper.jar"}}

  :aliases {"build-scraper" ["with-profile" "scraper" "uberjar"]})
