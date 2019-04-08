(ns librarian.cli
  (:require [librarian.scraper.io.config :as config]
            [librarian.scraper.io.scrape :as sscrape]
            [librarian.model.io.scrape :as mscrape]
            [librarian.model.core :as m]
            [librarian.scraper.attributes :as sattrs]
            [librarian.helpers.transaction :as tx]
            [clojure.pprint :as pp]
            [clojure.string :as string]
            [say-cheez.core :refer [capture-build-env-to]]
            [cli-matic.core :refer [run-cmd run-cmd*]])
  (:gen-class))

(capture-build-env-to BUILD)
(def PROJECT (:project BUILD))

(def ^:private ecosystems-string (string/join ", " (map name (keys m/ecosystems))))

(defn- mode-print
  [val mode]
  (case mode
    "pretty" (pp/pprint val)
    "edn" (prn val)
    (throw (Error. (str "Unknown mode " mode ".")))))

(defn scrape
  [{:keys [config out]}]
  (sscrape/create-scrape config out))

(defn print-scrape
  [{:keys [scrape mode]}]
  (mode-print (mscrape/read-scrape scrape) mode))

(defn print-config
  [{:keys [config mode]}]
  (mode-print (config/read-config config) mode))

(defn query
  [{:keys [scrape query mode]}]
  (mode-print (mscrape/query-file scrape query) mode))

(defn pull
  [{:keys [scrape selector eid mode]}]
  (mode-print (mscrape/pull-file scrape selector eid) mode))

(defn print-schema
  [{:keys [ecosystem]}]
  (if-let [{:keys [concept-aliases attribute-aliases attributes version]} (m/ecosystems ecosystem)]
    (do
      (println (str "Current " (name ecosystem) " schema version: " version))
      (println "\nAttributes:")
      (let [scraper-attrs-keys (keys sattrs/attributes)
            attribute-aliases (merge attribute-aliases
                                     (zipmap scraper-attrs-keys scraper-attrs-keys))
            attributes (merge attributes sattrs/attributes)]
        (pp/print-table ["attribute" "description" "uniq?" "ref?" "many?" "computed?"]
                        (->> attribute-aliases
                             (sort-by (comp #(str (namespace %) "/" (name %)) first))
                             (keep (fn [[alias ident]]
                                     (when-let [attr (attributes ident)]
                                       (when-not (:librarian/internal attr)
                                         {"attribute" alias
                                          "description" (:db/doc attr)
                                          "uniq?" (when (tx/unique-attr? attr) "✔")
                                          "ref?" (when (tx/ref-attr? attr) "✔")
                                          "many?" (when (= (:db/cardinality attr)
                                                           :db.cardinality/many)
                                                    "✔")
                                          "computed?" (when (:librarian/computed attr) "✔")})))))))
      (println "\nConcept types (possible values of the :type attribute):")
      (pp/pprint (sort (keys concept-aliases))))
    (throw (Exception. (str "Unknown ecosystem '" ecosystem "'. "
                            "Supported ecosystems: " ecosystems-string)))))

(def descs {:config "Path to a config file or to a directory containing a config named 'scraper.clj'."
            :scrape "Path to a scrape file or to a directory containing a scrape named 'scrape.db'."
            :mode "Printing mode: pretty or edn."
            :ecosystem (str "The name of a supported ecosystem. "
                            "Supported ecosystems: " ecosystems-string)})

(def CONFIGURATION
  {:app {:command (-> PROJECT :project symbol name)
         :description "Crawls through the documentation of software libraries and transforms them into a standardized format."
         :version (str (:version PROJECT) " (" (:git-build PROJECT) ")")}
   :commands [{:command "scrape" :short "s"
               :runs scrape
               :description "Scrapes a library using a scrape configuration and saves it."
               :opts [{:option "config" :as (:config descs) :short 0
                       :type :string, :default :present}
                      {:option "out" :short 1
                       :as (str "Optional scrape output path. "
                                "If not given, the scrape will be written into the config directory. "
                                "If a directory path is given, the scrape will be named 'scrape.db'.")
                       :type :string}]}
              {:command "print-scrape" :short "ps"
               :runs print-scrape
               :description "Prints a scrape to stdout."
               :opts [{:option "scrape" :as (:scrape descs) :short 0
                       :type :string, :default :present}
                      {:option "mode" :as (:mode descs) :short "m"
                       :type :string, :default "edn"}]}
              {:command "print-config" :short "pc"
               :description "Prints a config to stdout."
               :opts [{:option "config" :as (:config descs) :short 0
                       :type :string, :default :present}
                      {:option "mode" :as (:mode descs) :short "m"
                       :type :string, :default "pretty"}]
               :runs print-config}
              {:command "query" :short "q"
               :runs query
               :description "Performs a Datalog query on a given scrape file."
               :opts [{:option "scrape" :as (:scrape descs) :short 0
                       :type :string}
                      {:option "query" :short 1
                       :as "A Datalog query. See https://docs.datomic.com/on-prem/query.html."
                       :type :edn}
                      {:option "mode" :as (:mode descs) :short "m"
                       :type :string, :default "pretty"}]}
              {:command "pull" :short "p"
               :runs pull
               :description "Selects the given attributes of a given entity from a given scrape file."
               :opts [{:option "scrape" :as (:scrape descs) :short 0
                       :type :string}
                      {:option "selector" :short 1
                       :as "A pull selector. See https://docs.datomic.com/on-prem/pull.html."
                       :type :edn}
                      {:option "eid" :short 2
                       :as "The id that should be selected."
                       :type :edn}
                      {:option "mode" :as (:mode descs) :short "m"
                       :type :string, :default "pretty"}]}
              {:command "print-schema" :short "psm"
               :runs print-schema
               :description "Prints the database schema of a given ecosystem."
               :opts [{:option "ecosystem" :as (:ecosystem descs) :short 0
                       :type :keyword}]}]})

(defn main*
  "Like -main but does not terminate. For runtime use only."
  [& args]
  (run-cmd* CONFIGURATION args))

(defn -main
  [& args]
  (run-cmd args CONFIGURATION))
