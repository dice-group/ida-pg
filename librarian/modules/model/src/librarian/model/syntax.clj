(ns librarian.model.syntax
  "Implementation of the syntatic constructs used to define models via concepts, paradigms and ecosystems."
  (:require [clojure.spec.alpha :as s]
            [clojure.set :as set]
            [datascript.core :as d]
            [librarian.helpers.spec :as hs]
            [librarian.helpers.map :as map]
            [librarian.helpers.transaction :as tx]
            [librarian.model.common :as common]))

; Fns:

(defn- instance->tx
  [instance]
  (-> instance meta (:tx [instance]) vec))

(defn instances->tx
  "Transforms a collection of instances (created via instanciate* or instanciate) into a datascript transaction.
   The transaction will include all given instances and their nested subinstances.
   The resulting graph of concepts will be automatically processed and spec validated for consistency.
   Spec validation can be disabled if a tranaction for an incomplete/inconsistent set of concepts is needed."
  [root-instances & {:keys [^boolean check-specs]
                     :or {check-specs true}}]
  (let [raw-tx (mapcat instance->tx root-instances)
        instances (filter map? raw-tx)
        preproc-tx (into []
                         (mapcat (fn [instance]
                                   (let [{:keys [concept]} (meta instance)]
                                     (eduction (mapcat (fn [[attr processor]]
                                                         (when (contains? instance attr)
                                                           (processor (get instance attr)
                                                                      (:db/id instance)))))
                                               (:preprocess concept)))))
                         instances)
        schema (->> instances
                    (keep (comp :attributes :concept meta))
                    (apply merge common/attributes))
        tx (sort-by #(not (and (vector? %) (tx/indexing-tx? schema %)))
                    (concat raw-tx preproc-tx))
        {db :db-after, tempid->id :tempids} (d/with (d/empty-db schema) tx)
        id->tempid (set/map-invert tempid->id)
        postproc-tx (mapcat (fn [instance]
                              (let [{:keys [concept]} (meta instance)]
                                (when-let [processor (:postprocess concept)]
                                  (processor db (-> instance :db/id tempid->id)))))
                            instances)
        {db :db-after} (d/with db postproc-tx)]
    (when check-specs
      (doseq [instance instances
              :let [spec (-> instance meta :concept :spec)
                    e (d/entity db (-> instance :db/id tempid->id))]
              :when (and (not (:placeholder e)) (some? spec))]
        (when-not (s/valid? spec e)
          (throw (Error. (str "Invalid instance: " instance "\n"
                              (s/explain-str spec e)))))))
    (concat tx (map (partial tx/replace-ids-in-tx schema id->tempid)
                    postproc-tx))))

(defn instanciate*
  "Takes a concept and a map of namespaced or unnamespaced attributes and returns an instance of that concept.
   Instances can be nested and referenced by providing an instance as a value to a reference attribute.

   Exampe:
   ```
   (def my-instance (instanciate* my-concept
                      {:unnamespaced-attribute-name 1
                       :foo (instanciate* foo-concept {:bar \"baz\"})}))

   (instances->tx [my-instance]) ; => A datascript transaction to add the the instance and nested subinstances.
   ```"
  ([concept vals]
   (instanciate* identity concept vals))
  ([ref-map-handler concept vals]
   (let [attr-map (->> concept :attributes keys
                       (map (fn [key] [(keyword (name key)) key]))
                       (into {}))
         tempid (:db/id vals (d/tempid nil))
         [vals tx] (reduce (fn [[vals tx] [k v]]
                             (if (= (get (name k) 0) \_)
                               [vals
                                (let [v (if (and (map? v) (not (-> v meta :tx)))
                                          (ref-map-handler v)
                                          v)]
                                  (cond-> tx
                                    true (conj [:db/add (:db/id v v)
                                                (keyword (namespace k) (subs (name k) 1))
                                                tempid])
                                    (map? v) (into (instance->tx v))))]
                               (let [attr (attr-map k k)
                                     attr-desc (get-in concept [:attributes attr])
                                     ref (tx/ref-attr? attr-desc)
                                     many (tx/many-attr? attr-desc)
                                     unique (tx/unique-attr? attr-desc)
                                     [val insts tx-add]
                                     (cond
                                       (not ref) [v [] (when unique [[:db/add tempid attr v]])]
                                       (or (not many) (map? v) (not (coll? v)))
                                       (let [v (if (map? v) (ref-map-handler v) v)
                                             vid (:db/id v v)]
                                         [vid (if (map? v) [v] [])
                                          (when unique [[:db/add tempid attr vid]])])
                                       :else
                                       (let [v (map #(if (map? %) (ref-map-handler %) %) v)
                                             vids (mapv #(:db/id % %) v)
                                             tx-add (when unique (mapv (fn [vid] [:db/add tempid attr vid]) vids))]
                                         [vids (filter map? v) tx-add]))]
                                 [(conj vals [attr val])
                                  (-> tx
                                      (into tx-add)
                                      (into (mapcat instance->tx) insts))])))
                           [[] #{}] vals)
         instance (into {:db/id tempid
                         :type (:ident concept)}
                        vals)]

     ; Add a datascript transaction for the instance.
     ; The instance itself is part of the transaction and gets a concept reference.
     ; This attached metadata is an implementation detail.
     (with-meta instance {:tx (conj tx (with-meta instance {:concept concept}))}))))

(defn instanciate
  "Syntactic sugar for instanciate* where the `vals`-map does not need to be wrapped in curly-braces."
  [concept & {:as vals}]
  (instanciate* concept vals))

(defn instanciate-with-ecosystem
  "Instanciates a concept description using a given ecosystem.
   All concept names and attributes can be provided via their alias.
   All concept attributes have to be either fully-qualified or namespaced with a concept alias.
   The concept will be resolved from the ecosystem by providing its alias keyword at :type.
   Concepts can be nested simply by nesting maps of concept-descs.
   The returned instance can be used like the ones returned by instanciate."
  ([ecosystem concept-desc]
   (instanciate-with-ecosystem ecosystem (fn [_ m] m) concept-desc))
  ([{:keys [attribute-aliases concepts] :as ecosystem} transformer concept-desc]
   (let [concept (map/get-or-fail concepts (:type concept-desc))
         concept-desc (dissoc concept-desc :type)
         concept-desc (map/map-k (fn [k]
                                   (let [kname (name k)]
                                     (if (= (first kname) \_)
                                       (keyword (namespace (map/get-or-fail attribute-aliases
                                                                            (keyword (namespace k)
                                                                                     (subs kname 1))))
                                                kname)
                                       k)))
                                 concept-desc)]
     (instanciate* #(instanciate-with-ecosystem ecosystem transformer %)
                   concept (transformer concept concept-desc)))))

(defn- merge-concepts
  "Merges the given `concept` with the concept collection `extends` and returns a single merged concept."
  [concept extends]
  (let [merged (apply map/merge-by-key
                      {:attributes merge
                       :spec hs/and
                       :preprocess (partial map/merge-by-key tx/merge-direct)
                       :postprocess tx/merge}
                      (conj extends concept))
        merged (if (contains? merged :spec)
                 merged
                 (assoc merged :spec any?))
        merged (assoc merged :extends (map :ident extends))]
    merged))

(defn- merge-paradigms
  "Merges the given `paradigm` with the paradigm collection `extends` and returns a single merged paradigm"
  ([paradigm extends]
   (merge-paradigms (conj extends (update paradigm :builtins
                                          #(when % [(instances->tx %)])))))
  ([paradigms]
   (apply map/merge-by-key
          {:concepts merge
           :builtins (comp vec concat)}
          paradigms)))

(defn- no-attribute-collisions?
  "Checks whether no attribute name is used twice in the given concept and the concepts it extends.
   E.g. it would not be ok if concept x inherits from y and both have an attribute z (:x/z and :y/z).
   This important to guarantee that attribute aliases are unique for each concept since in the previous example :y/z would be aliased to :x/z which would create ambiguity."
  [{:keys [concept extends]}]
  (let [attr-names (->> (conj extends concept)
                        (mapcat (comp keys :attributes))
                        (map name))]
    (or (empty? attr-names)
        (apply distinct? attr-names))))

(def ^:private common-attribute-aliases
  (as-> (keys common/attributes) $
        (zipmap $ $)))

(defn paradigm->ecosystem
  "Converts a given paradigm into an ecosystem."
  [{:keys [concepts builtins generate executor]}]
  {:concepts concepts
   :builtins builtins
   :generate generate
   :executor executor
   :concept-aliases (map/map-v :ident concepts)
   :attribute-aliases (into common-attribute-aliases
                            (for [[c {:keys [attributes]}] concepts
                                  :let [cns (namespace c)
                                        cns (str (when cns (str cns "."))
                                                 (name c))]
                                  attribute (keys attributes)]
                              [(keyword cns (name attribute)) attribute]))
   :attributes (reduce-kv (fn [a _ {:keys [attributes]}]
                            (into a attributes))
                          common/attributes concepts)
   :specs (map/map-kv (comp (juxt :ident :spec) second) concepts)
   :preprocessors  (map/keep-kv (comp (juxt :ident :preprocess) second) concepts)
   :postprocessors (map/keep-kv (comp (juxt :ident :postprocess) second) concepts)})

(defn merge-ecosystems
  "Merges the given ecosystem into a single ecosystem."
  [& ecosystems]
  (->> ecosystems
       (map #(select-keys % [:concepts :builtins]))
       (apply merge-paradigms)
       paradigm->ecosystem))

; Macros:

(defmacro defconcept
  "Defines a new concept with name `name` in the current namespace.
   The concept is described by the sequence of key-value pairs `concept-desc`.
   A concept description can contain the following pairs, all of which are optional:
   - `:attributes`: A map describing datascript attributes, i.e. a subset of a database schema.
   - `:spec`: A Clojure spec that should be used to check the validity of supposed instances of this concept.
   - `:preprocess`: A map from attributes to preprocessor functions for those attributes. Useful to mirror attribute values.
   - `:postprocess`: A function that takes a datascript database and the id of an instance of this concept. The function returns a datascript transaction that should be executed as part of the transaction that adds the given concept. Useful to compute derived attributes for concept instances.

   In addition to the key-value pairs the concept description can be preceded by a vector of concepts that the newly defined concept should inherit from.

   Example:
   ```
   (defconcept my-child-concept [parent-concept1 parent-concept2]
     :attributes {::x {:db/doc \"A test attribute.\"}
                  ::y {:db/valueType :db.type/ref
                       :db/doc \"A reference to another concept.\"}}
     :postprocess (fn [db id] [:db/add id ::x \"All instances should have the same x-attribute value.\"]))
   ```"
  [name & concept-desc]
  `(let [concept-desc# ~(vec concept-desc)
         conformed# (s/conform ::concept-desc concept-desc#)]
     (if (s/invalid? conformed#)
       (throw (Exception. (str "Invalid concept definition.\n"
                               (s/explain-str ::concept-desc concept-desc#))))
       (let [name# (quote ~name)
             name# (if (qualified-symbol? name#)
                     (keyword name#)
                     (keyword (str *ns*) (str name#)))
             concept# (-> conformed#
                          (assoc :ident name#)
                          (dissoc :extends))]
         (doseq [parent# (:extends conformed#)
                 :when (not (isa? name# parent#))]
           (derive name# parent#))
         (def ~name concept#)))))

(defmacro defparadigm
  "Defines a new paradigm with name `name` in the current namespace.
   The name is followed by an optional vector of paradigms that should be included in the new paradigm.
   Then a sequence of key-value pairs follows:
   - `:concepts`: A map of unnamespaced concept alias keywords to concepts.
   - `:builtins`: A collection of builtin concept instances in the defined paradigm. The predefined instances should be created via `instanciate`.

   Example:
   ```
   (defparadigm my-logical-programming-oop-paradigm [object-oriented-paradigm logical-paradigm]
     :concepts {:logical-class my-logical-class-concept
                :predicate-method my-predicate-method-concept}
     :builtins [(instanciate my-logical-class-concept :name \"Predicate\")])
   ```"
  [name & paradigm]
  `(let [paradigm# ~(vec paradigm)
         conformed# (s/conform ::paradigm-desc paradigm#)]
     (if (s/invalid? conformed#)
       (throw (Exception. (str "Invalid paradigm definition.\n"
                               (s/explain-str ::paradigm-desc paradigm#))))
       (def ~name conformed#))))

(defmacro defecosystem
  "Defines a new ecosystem with name `name` in the current namespace.
   Like `defparadigm` but accepts additional key-value pair types:
   - `:generate`: A function that takes a metadata map and a database containing an executable CFG and that returns a snippet of executable code for the ecosystem.
   - `:executor`: Similar to the generator defined above but returns a function that executes the code snippet and returns the result of the execution instead of simply returning the code snippet string."
  [name & ecosystem]
  `(let [ecosystem# ~(vec ecosystem)
         conformed# (s/conform ::ecosystem-desc ecosystem#)]
     (if (s/invalid? conformed#)
       (throw (Exception. (str "Invalid ecosystem definition.\n"
                               (s/explain-str ::ecosystem-desc ecosystem#))))
       (def ~name conformed#))))

; Specs:

(s/def ::concept (hs/keys* :opt-un [::attributes ::spec ::preprocess ::postprocess]))
(s/def ::attributes (s/every-kv keyword? any?))
(s/def ::postprocess fn?)
(s/def ::preprocess (s/every-kv keyword? fn?))
(s/def ::concept-desc (s/and (s/cat :extends (s/? (hs/vec-of ::concept))
                                    :concept (s/* any?))
                             (s/keys :opt-un [::concept])
                             no-attribute-collisions?
                             (s/conformer (fn [{:keys [concept extends]}]
                                            (merge-concepts concept extends)))))
(s/def ::concepts (s/map-of keyword? ::concept))

(s/def ::paradigm-desc (s/and (s/cat :extends (s/? (hs/vec-of ::paradigm))
                                     :paradigm (s/* any?))
                              (s/keys :opt-un [::paradigm])
                              (s/conformer (fn [{:keys [paradigm extends]
                                                 :or {paradigm {}}}]
                                             (merge-paradigms paradigm extends)))))
(s/def ::paradigm (s/and (hs/keys* :opt-un [::concepts ::builtins ::generate ::executor])))
(s/def ::builtins coll?)
(s/def ::generate (some-fn fn? var?))
(s/def ::executor (some-fn fn? var?))

(s/def ::ecosystem-desc (s/and ::paradigm-desc
                               (s/conformer paradigm->ecosystem)))
