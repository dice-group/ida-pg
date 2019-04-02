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
                 [librarian/helpers]
                 [librarian/model]]
  :repositories [["oracleReleases" {:url "https://download.oracle.com/maven"}]]

  :modules {:parent "../.."}

  :aot [librarian.scraper.crawler.factory])
