(ns librarian.model.io.scrape
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [clojure.spec.alpha :as s]
            [clojure.edn :as edn]
            [clojure.set :as set]
            [me.raynes.fs :as fs]
            [datascript.db :as db]
            [semver.core :as semver]
            [librarian.model.core :as m]
            [librarian.model.db :as mdb])
  (:import (java.util.zip GZIPInputStream
                          GZIPOutputStream)))

(def default-name "scrape.db")

(defn- get-file
  [file]
  (cond
    (fs/directory? file) (fs/file file default-name)
    (fs/file? file) (fs/file file)
    :else (throw (Error. "Invalid scrape path."))))

(defn write-scrape
  [file {:keys [name ecosystem meta]} scrape]
  (let [file (get-file file)
        out {:db scrape
             :name name
             :created (java.util.Date.)
             :ecosystem (:alias ecosystem)
             :ecosystem/version (:version ecosystem)
             :meta meta}]
    (log/info (str "Writing scrape to " file "..."))
    (io/make-parents file)
    (with-open [data (-> file
                         (io/output-stream)
                         (GZIPOutputStream.))]
      (io/copy (pr-str out) data))
    (log/info (str "Successfully wrote scrape to " file "."))
    file))

(defn read-scrape
  [file]
  (let [file (get-file file)
        scrape (with-open [data (-> file
                                    (io/input-stream)
                                    (GZIPInputStream.))]
                 ; Read scrape but do not convert db yet, to allow for schema updates:
                 (edn/read-string {:readers {'datascript/DB identity}}
                                  (slurp data)))
        conformed (s/conform ::scrape scrape)]
    (if (s/invalid? conformed)
      (throw (Exception. (str "Invalid scrape."
                              (s/explain-str ::scrape scrape))))
      conformed)))

(defn query-scrape
  [scrape query & args]
  (let [{:keys [db ecosystem]} scrape]
    (apply mdb/q ecosystem query db args)))

(defn query-file
  [scrape-file query & args]
  (apply query-scrape (read-scrape scrape-file) query args))

(defn pull-scrape
  [scrape selector eid]
  (let [{:keys [db ecosystem]} scrape]
    (mdb/pull ecosystem db selector eid)))

(defn pull-file
  [scrape-file selector eid]
  (pull-scrape (read-scrape scrape-file) selector eid))

(defn- semver-compat
  [v1 v2]
  (= (-> v1 semver/parse :major)
     (-> v2 semver/parse :major)))

(defn- patch-db
  [{:keys [schema datoms]} ecosystem]
  (let [new-schema (:attributes ecosystem)
        [schema-keys new-schema-keys] (map (comp set keys) [schema new-schema])
        key-diff (filter (comp not :librarian/temporary schema)
                         (set/difference schema-keys new-schema-keys))]
    (if (empty? key-diff)
      (db/db-from-reader {:schema new-schema, :datoms datoms})
      (throw (Exception. (str "Scrape schema is incompatible with current ecosystem model.\n"
                              "Scrape schema attributes: " schema-keys "\n"
                              "Model schema attributes: " new-schema-keys "\n"
                              "Missing in model schema: " (seq key-diff)))))))

(s/def ::scrape
       (s/and (s/keys :req-un [::db
                               ::name
                               ::created
                               ::ecosystem
                               ::meta])
              #(semver-compat (get-in % [:ecosystem :version])
                              (:ecosystem/version %))
              (s/conformer #(update % :db patch-db (:ecosystem %)))))

(s/def ::db (s/keys :req-un [::schema ::datoms]))
(s/def ::name #(or (string? %) (symbol? %)))
(s/def ::created inst?)
(s/def ::ecosystem (s/and keyword?
                          #(contains? m/ecosystems %)
                          (s/conformer m/ecosystems)))
(s/def ::meta map?)
