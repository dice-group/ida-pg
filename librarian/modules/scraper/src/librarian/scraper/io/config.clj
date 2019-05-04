(ns librarian.scraper.io.config
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [clojure.walk :as w]
            [me.raynes.fs :as fs]
            [hickory.select]
            [librarian.helpers.zip :as hzip]
            [librarian.helpers.predicate :as predicate]
            [librarian.helpers.map :as map]
            [librarian.model.syntax :as msyntax]
            [librarian.model.io.scrape :as mscrape]
            [librarian.model.concepts.snippet :as snippet]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.semantic-type :as semantic-type]
            [librarian.scraper.match :as match])
  (:import (java.util.regex Pattern)))

(def default-name "scraper.clj")

(def ^:private ^:dynamic *full-config-parse* true)

(defn index-hooks
  [hooks patterns]
  (->> hooks
       (map (fn [hook]
              (if-let [pattern (patterns (:pattern hook))]
                (merge pattern hook)
                hook)))
       (mapcat (fn [hook]
                 (if (seqable? (:trigger hook))
                   (map (partial assoc hook :trigger) (:trigger hook))
                   [hook])))
       (group-by :trigger)))

(defn resolve-hook-aliases
  [hooks {:keys [attributes concept-aliases attribute-aliases]}]
  (let [concept-keys [:concept]
        attr-keys [:attribute :ref-from-trigger :ref-to-trigger]
        concept-lookup (partial map/get-or-fail concept-aliases)
        attr-lookup (fn [attr]
                      (if (seqable? attr)
                        (map (partial map/get-or-fail attribute-aliases) attr)
                        (map/get-or-fail attribute-aliases attr)))]
    (map/map-kv (fn [[k v]]
                  [(map/get-or-fail (merge concept-aliases
                                           attribute-aliases
                                           {:document :document})
                                    k)
                   (mapv (fn [hook]
                           (let [hook (-> hook
                                          (map/update-keys concept-keys concept-lookup)
                                          (map/update-keys attr-keys attr-lookup))]
                             (if (not-any? #(-> (get hook %) attributes :librarian/computed) attr-keys)
                               hook
                               (throw (Error. (str "Invalid hook " hook
                                                   ". Computed attributes cannot be scraped."))))))
                         v)])
                hooks)))

(defn- instanciate-snippet-part
  [snippet-part concepts snippet-instance]
  (let [concept (map/get-or-fail concepts (:type snippet-part))
        concept-ident (:ident concept)
        snippet-part (dissoc snippet-part :type)]
    (msyntax/instanciate* #(instanciate-snippet-part % concepts snippet-instance)
                          concept
                          (if (or (isa? concept-ident (:ident call/call))
                                  (isa? concept-ident (:ident call-parameter/call-parameter))
                                  (isa? concept-ident (:ident call-result/call-result))
                                  (isa? concept-ident (:ident semantic-type/semantic-type)))
                            (assoc snippet-part ::snippet/_contains snippet-instance)
                            snippet-part))))

(defn snippet->tx
  [snippet {:keys [concepts]}]
  (let [snippet-instance (msyntax/instanciate snippet/snippet)]
    (msyntax/instances->tx (map #(instanciate-snippet-part % concepts snippet-instance)
                                snippet))))

(defn cache-id
  [config conformed]
  (let [hashable-config (w/postwalk (fn [form]
                                      (cond
                                        (instance? Pattern form) (str form)
                                        (and (symbol? form)
                                             (= (last (name form)) \#)
                                             (clojure.string/includes? (name form) "__"))
                                        '_
                                        :else form))
                                    (select-keys config [:seed :max-pages :max-depth
                                                         :should-visit :hooks :ecosystem]))]
    (hash-combine (-> conformed :meta (:librarian/cache-id 0))
                  (assoc hashable-config :ecosystem/version
                         (-> conformed :ecosystem :version)))))

(defn parse-config
  [config]
  (let [conformed (binding [*full-config-parse* false]
                    ; don't perform full hook and snippet parsing on extended configs:
                    (s/conform ::config config))
        conformed (assoc-in conformed [:meta :librarian/cache-id]
                            (cache-id config conformed))]
    (if (s/invalid? conformed)
      (throw (Exception. (str "Invalid scraper configuration.\n"
                              (s/explain-str ::config config))))
      (if *full-config-parse*
        (let [ecosystem (:ecosystem conformed)
              hooks (-> (:hooks conformed)
                        (index-hooks (:patterns conformed))
                        (resolve-hook-aliases ecosystem))
              snippets (map #(snippet->tx % ecosystem)
                            (:snippets conformed))]
          (assoc conformed
                 :hooks hooks
                 :snippets snippets))
        conformed))))

(defmacro defscraper
  [name & {:as config}]
  `(def ~name ~(parse-config (assoc config :name name))))

(defn read-config
  [path]
  (binding [*read-eval* false]
    (let [file (cond
                 (fs/directory? path) (fs/file path default-name)
                 (fs/file? path) (fs/file path)
                 :else (throw (Error. "Invalid config path.")))
          file (fs/normalized file)
          _ (log/info (str "Reading config from " file "..."))
          config (read-string (slurp file))
          {:keys [name config]} (s/conform ::config-outer config)]
      (fs/with-cwd (fs/parent file)
        (parse-config (assoc config :name name))))))

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
                             :config (s/keys*)))

(s/def ::name ::mscrape/name)
(s/def ::ecosystem ::mscrape/ecosystem)
(s/def ::meta ::mscrape/meta)
(s/def ::extends string?)
(s/def ::seed string?)
(s/def ::max-pages int?)
(s/def ::max-depth int?)

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

(s/def ::select (s/and seq? (s/conformer #(eval (w/postwalk-replace select-kws %)))))
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

(s/def ::snippets (s/coll-of ::snippet))
(s/def ::snippet (s/coll-of ::snippet-instance))
(s/def ::snippet-instance (s/keys :req-un [::type]))
(s/def ::type keyword?)

(s/def ::config
       (s/and (s/or :extension
                    (s/and (s/keys :req-un [::extends]
                                   :opt-un [::ecosystem
                                            ::seed
                                            ::should-visit
                                            ::hooks
                                            ::meta
                                            ::patterns
                                            ::max-pages
                                            ::max-depth
                                            ::snippets])
                           (s/conformer #(merge (read-config (:extends %)) %)))
                    :root
                    (s/keys :req-un [::ecosystem
                                     ::seed
                                     ::should-visit
                                     ::hooks]
                            :opt-un [::meta
                                     ::patterns
                                     ::max-pages
                                     ::max-depth
                                     ::snippets]))
              (s/conformer second)
              (s/every-kv keyword? any?)))
