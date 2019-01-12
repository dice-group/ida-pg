(ns lib-scraper.core
  (:require [lib-scraper.io.core :as io]
            [lib-scraper.io.config :as config]
            [lib-scraper.io.scrape :as scrape]
            [clojure.pprint :as pp]
            [say-cheez.core :refer [capture-build-env-to]]
            [cli-matic.core :refer [run-cmd run-cmd*]])
  (:gen-class))

(capture-build-env-to BUILD)
(def PROJECT (:project BUILD))

(defn- mode-print
  [val mode]
  (case mode
    "pretty" (pp/pprint val)
    "edn" (prn val)
    (throw (Error. (str "Unknown mode " mode ".")))))

(defn scrape
  [{:keys [config out]}]
  (io/create-scrape config out))

(defn print-scrape
  [{:keys [scrape mode]}]
  (mode-print (scrape/read-scrape scrape) mode))

(defn print-config
  [{:keys [config mode]}]
  (mode-print (config/read-config config) mode))

(def descs {:config "Path to a config file or to a directory containing a config named 'scraper.clj'."
            :mode "Printing mode (pretty or edn)." })

(def CONFIGURATION
  {:app {:command (:project PROJECT)
         :description "Crawls through the documentation of software libraries and transforms them into a standardized format."
         :version (str (:version PROJECT) " (" (:git-build PROJECT) ")")}
   :commands [{:command "scrape" :short "s"
               :description "Scrapes a library using a scrape configuration and saves it."
               :opts [{:option "config" :as (:config descs) :short 0
                       :type :string, :default :present}
                      {:option "out" :short 1
                       :as "Scrape output path. If not given, the scrape will be written into the config directory."
                       :type :string}]
               :runs scrape}
              {:command "print-scrape" :short "ps"
               :description "Prints a scrape to stdout."
               :opts [{:option "scrape" :as "Scrape path." :short 0
                       :type :string, :default :present}
                      {:option "mode" :as (:mode descs) :short "m"
                       :type :string, :default "edn"}]
               :runs print-scrape}
              {:command "print-config" :short "pc"
               :description "Prints a config to stdout."
               :opts [{:option "config" :as (:config descs) :short 0
                       :type :string, :default :present}
                      {:option "mode" :as (:mode descs) :short "m"
                       :type :string, :default "pretty"}]
               :runs print-config}]})

(defn main*
  "Like -main but does not terminate. For runtime use only."
  [& args]
  (run-cmd* CONFIGURATION args))

(defn -main
  [& args]
  (run-cmd args CONFIGURATION))
