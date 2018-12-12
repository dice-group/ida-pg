(defproject lib-scraper "0.1.0-SNAPSHOT"
  :description "Crawles through the documentation of software libraries and transforms them into a standardized format."
  :url "https://github.com/dice-group/ida"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [edu.uci.ics/crawler4j "4.4.0"]
                 [hickory "0.7.1"]
                 [datascript "0.16.9"]]

  :plugins [[lein-cljfmt "0.6.2"]]

  :main lib-scraper.core
  :aot [lib-scraper.crawler.factory]

  :profiles {:dev {:source-paths ["dev" "src" "test"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [proto-repl "0.3.1"]
                                  [proto-repl-charts "0.3.1"]
                                  [proto-repl-sayid "0.1.3"]]
                   :repl-options {:init-ns user
                                  :init (start)}
                   :eastwood {:exclude-linters []}}
             :uberjar {:aot :all}})
