(ns librarian.generator.interop
  (:require [librarian.generator.core :as gc]
            [clojure.edn :as edn])
  (:gen-class :name librarian.generator.Generator
              :prefix "generator-"
              :state scrape
              :init init
              :factory from
              :main false
              :constructors {[librarian.model.Scrape] []}
              :methods [[search [String long] Object]
                        [search [Iterable long] Object]
                        ^:static [solver [Object] clojure.lang.IFn]
                        [searchSolver [String long] clojure.lang.IFn]
                        [searchSolver [Iterable long] clojure.lang.IFn]
                        ^:static [code [Object] String]
                        [searchCode [String long] String]
                        [searchCode [Iterable long] String]]))

(defn parse-init-descs
  [init-descs]
  (if (string? init-descs)
    (edn/read-string init-descs)
    init-descs))

(defn generator-init
  [scrape]
  [[] (.scrape scrape)])

(defn generator-search
  [this init-descs ^long limit]
  (gc/search (.scrape this) (parse-init-descs init-descs) limit))

(defn generator-solver
  [this solution]
  (gc/solver solution))

(defn generator-code
  [this solution]
  (gc/code solution))

(defn generator-searchSolver
  [this init-descs ^long limit]
  (gc/search-solver (.scrape this) (parse-init-descs init-descs) limit))

(defn generator-searchCode
  [this init-descs ^long limit]
  (gc/search-code (.scrape this) (parse-init-descs init-descs) limit))
