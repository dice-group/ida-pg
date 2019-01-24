(ns lib-scraper.io.config
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [clojure.walk :as w]
            [me.raynes.fs :as fs]
            [hickory.select]
            [lib-scraper.model.core :as m]
            [lib-scraper.helpers.zip :as hzip]
            [lib-scraper.helpers.predicate :as predicate]
            [lib-scraper.scraper.match :as match])
  (:import (java.util.regex Pattern)))

(def default-name "scraper.clj")

(defn defscraper*
  [name config]
  (let [conformed (s/conform ::config config)]
    (if (s/invalid? conformed)
      (throw (Exception. (str "Invalid scraper configuration.\n"
                              (s/explain-str ::config config))))
      (assoc conformed :name (str name)))))

(defmacro defscraper
  [name & config]
  `(def ~name ~(defscraper* name config)))

(defn read-config
  [path]
  (binding [*read-eval* false]
    (let [file (cond
                 (fs/directory? path) (fs/file path default-name)
                 (fs/file?) (fs/file path)
                 :else (throw (Error. "Invalid config path.")))
          file (fs/normalized file)
          _ (log/info (str "Reading config from " file "..."))
          config (read-string (slurp file))
          {:keys [name config]} (s/conform ::config-outer config)]
      (fs/with-cwd (fs/parent file)
        (defscraper* name config)))))

(defn- parse-should-visit
  [expr]
  (cond
    (fn? expr) expr
    (or (string? expr) (instance? Pattern expr))
    (match/match-url expr)
    :else
    (predicate/parse {'require-classes match/require-classes
                      'match-url match/match-url}
                     expr)))

(s/def ::config-outer (s/cat :defscraper #(= % 'defscraper)
                             :name ::name
                             :config (s/* any?)))

(s/def ::ecosystem (s/and keyword?
                          #(contains? m/ecosystems %)
                          (s/conformer m/ecosystems)))

(s/def ::name #(or (string? %) (symbol? %)))
(s/def ::extends string?)
(s/def ::seed string?)
(s/def ::max-pages int?)
(s/def ::max-depth int?)
(s/def ::meta map?)

(s/def ::should-visit (s/and (s/conformer parse-should-visit) fn?))

(s/def ::hook (s/and (s/keys :opt-un [::trigger ::selector
                                      ::concept
                                      ::ref-to-trigger
                                      ::ref-from-trigger
                                      ::attribute
                                      ::value
                                      ::transform
                                      ::pattern
                                      ::allow-incomplete])))

(s/def ::trigger (s/and (s/or :single keyword?
                              :multiple (s/coll-of keyword?))
                        (s/conformer second)))

(s/def ::concept keyword?)
(s/def ::ref-to-trigger keyword?)
(s/def ::ref-from-trigger keyword?)
(s/def ::attribute #(or (keyword? %) (every? keyword? %)))
(s/def ::value (partial contains? #{:content :trigger-index}))
(s/def ::pattern keyword?)
(s/def ::allow-incomplete boolean?)
(s/def ::hooks (s/coll-of ::hook))
(s/def ::patterns (s/map-of keyword? ::hook))

(def select-kws (ns-publics 'hickory.select))

(s/def ::spread-op (partial contains? (set (keys hzip/step-types))))
(s/def ::limit int?)
(s/def ::drop int?)

(s/def ::select (s/and seq?
                       (s/conformer (fn [form]
                                      (eval (w/postwalk-replace select-kws form))))))
(s/def ::while ::select)

(s/def ::spread
       (s/and (s/or :basic ::spread-op
                    :complex (s/and (s/cat :spread-op ::spread-op
                                           :args (s/keys* :opt-un [::select ::while ::drop ::limit]))
                                    (s/conformer (fn [{:keys [spread-op args]}]
                                                   (into [spread-op] (flatten (seq args)))))))
              (s/conformer second)))

(s/def ::selector-part (s/and (s/or :spread ::spread
                                    :select ::select)
                              (s/conformer second)))

(s/def ::selector (s/and seqable?
                         (s/coll-of ::selector-part)))

(s/def ::fn-form (s/and seq?
                        #(contains? #{'fn* 'fn} (first %))
                        (s/conformer eval)))

(s/def ::pattern-fn (s/and #(instance? Pattern %)
                           (s/conformer (fn [p] #(re-find p %)))))

(s/def ::transform (s/and (s/or :fn fn?
                                :fn-form ::fn-form
                                :pattern ::pattern-fn)
                          (s/conformer second)))

(s/def ::config
       (s/and (s/or :extension
                    (s/and (s/keys* :req-un [::extends]
                                    :opt-un [::ecosystem
                                             ::seed
                                             ::should-visit
                                             ::hooks
                                             ::meta
                                             ::patterns
                                             ::max-pages
                                             ::max-depth])
                           (s/conformer #(merge (read-config (:extends %)) %)))
                    :root
                    (s/keys* :req-un [::ecosystem
                                      ::seed
                                      ::should-visit
                                      ::hooks]
                             :opt-un [::meta
                                      ::patterns
                                      ::max-pages
                                      ::max-depth]))
              (s/conformer second)
              (s/every-kv keyword? any?)))
