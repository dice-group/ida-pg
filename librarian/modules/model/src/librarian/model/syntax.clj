(ns librarian.model.syntax
  (:require [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.helpers.map :as map]
            [librarian.helpers.transaction :as tx]
            [librarian.model.common :as common]))

; Fns:

(defn- merge-concepts
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
  [paradigm extends]
  (apply merge (conj extends paradigm)))

(defn- no-attribute-collisions?
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
  [paradigm]
  {:paradigm paradigm
   :concept-aliases (map/map-v :ident paradigm)
   :attribute-aliases (into common-attribute-aliases
                            (for [[c {:keys [attributes]}] paradigm
                                  :let [cns (namespace c)
                                        cns (str (when cns (str cns "."))
                                                 (name c))]
                                  attribute (keys attributes)]
                              [(keyword cns (name attribute)) attribute]))
   :attributes (reduce-kv (fn [a _ {:keys [attributes]}]
                            (into a attributes))
                          common/attributes paradigm)
   :specs (map/map-kv (comp (juxt :ident :spec) second) paradigm)
   :preprocessors  (map/keep-kv (comp (juxt :ident :preprocess) second) paradigm)
   :postprocessors (map/keep-kv (comp (juxt :ident :postprocess) second) paradigm)})

(defn merge-ecosystems
  [& ecosystems]
  (->> ecosystems
       (map :paradigm)
       (apply merge)
       paradigm->ecosystem))

; Macros:

(defmacro defconcept
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
  [name & paradigm]
  `(let [paradigm# ~(vec paradigm)
         conformed# (s/conform ::paradigm-desc paradigm#)]
     (if (s/invalid? conformed#)
       (throw (Exception. (str "Invalid paradigm definition.\n"
                               (s/explain-str ::paradigm-desc paradigm#))))
       (def ~name conformed#))))

(defmacro defecosystem
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
(s/def ::concepts (s/and (hs/keys*) (s/map-of keyword? ::concept)))

(s/def ::paradigm-desc (s/and (s/cat :extends (s/? (hs/vec-of ::paradigm))
                                     :paradigm (s/* any?))
                              (s/keys :opt-un [::paradigm])
                              (s/conformer (fn [{:keys [paradigm extends]
                                                 :or {paradigm {}}}]
                                             (merge-paradigms paradigm extends)))))
(s/def ::paradigm ::concepts)

(s/def ::ecosystem-desc
       (s/and ::paradigm-desc (s/conformer paradigm->ecosystem)))
