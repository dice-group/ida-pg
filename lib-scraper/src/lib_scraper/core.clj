(ns lib-scraper.core
  (:require [hickory.select :as s]
            [lib-scraper.scraper.core :refer [scrape-and-store! load-stored]]
            [lib-scraper.io.config :as config]))

(defn scrape-skl!
  []
  (let [skl-spec (config/read-config "libs/scikit-learn/scraper.clj")]
    (scrape-and-store! skl-spec "libs/scikit-learn/scrapedb.edn")
    (load-stored "libs/scikit-learn/scrapedb.edn")))
