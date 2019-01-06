(ns lib-scraper.model.syntax
  (:require [clojure.spec.alpha :as s]
            [clojure.set :as set]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.helpers.map :as map]
            [lib-scraper.helpers.transaction :as tx]
            [lib-scraper.model.common :as common]))

(defn merge-concepts
  [concept extends]
  (let [merged (apply map/merge-by-key
                      {:attributes merge
                       :spec hs/and
                       :postprocess tx/merge
                       :extends set/union}
                      (conj extends concept))]
    (if (contains? merged :spec)
      merged
      (assoc merged :spec any?))))

(defn merge-paradigms
  [paradigm extends]
  (apply merge-with merge (conj extends paradigm)))

(defn no-attribute-collisions?
  [{:keys [concept extends]}]
  (let [attr-names (->> (conj extends concept)
                        (mapcat (comp keys :attributes))
                        (map name))]
    (or (empty? attr-names)
        (apply distinct? attr-names))))

(defn ecosystem-desc->ecosystem
  [m]
  (let [aliases (map/map-v :ident m)
        aliases (into aliases
                      (for [[c {:keys [attributes]}] m
                              :let [cns (namespace c)
                                    cns (str (if cns (str cns "."))
                                             (name c))]
                              attribute (keys attributes)]
                          [(keyword cns (name attribute)) attribute]))]
    {:aliases aliases
     :extends (map/map-kv (comp (juxt :ident :extends) second) m)
     :attributes (reduce-kv (fn [a _ {:keys [attributes]}]
                              (into a attributes))
                            common/attributes m)
     :specs (map/map-kv (comp (juxt :ident :spec) second) m)
     :postprocessors (map/keep-kv (comp (juxt :ident :postprocess) second) m)}))

(s/def ::concept (hs/keys* :opt-un [::attributes ::spec ::postprocess]))
(s/def ::attributes (s/every-kv keyword? any?))
(s/def ::postprocess fn?)
(s/def ::concept-desc (s/and (s/cat :extends (s/? (hs/vec-of ::concept))
                                    :concept (s/* any?))
                             (s/keys :opt-un [::concept])
                             no-attribute-collisions?
                             (s/conformer (fn [{:keys [concept extends]}]
                                            (merge-concepts concept extends)))))
(s/def ::concepts (s/and (hs/keys*) (s/map-of keyword? ::concept)))

(s/def ::paradigm-desc (s/and (s/cat :extends (s/? (hs/vec-of ::paradigm))
                                     :paradigm (s/* any?))
                              (s/keys :req-un [::paradigm])
                              (s/conformer (fn [{:keys [paradigm extends]}]
                                             (merge-paradigms paradigm extends)))))
(s/def ::paradigm ::concepts)

(s/def ::ecosystem-desc
       (s/and ::paradigm-desc (s/conformer ecosystem-desc->ecosystem)))

(defmacro defconcept
  [name & concept-desc]
  `(let [concept-desc# ~(vec concept-desc)
         conformed# (s/conform ::concept-desc concept-desc#)]
     (if (s/invalid? conformed#)
       (throw (Exception. (str "Invalid concept definition.\n"
                               (s/explain-str ::concept-desc concept-desc#))))
       (let [name# (quote ~name)
             name# (if (qualified-symbol? name#)
                     (keyword name#) (keyword (str *ns*) (str name#)))
             concept# (-> conformed#
                          (assoc :ident name#)
                          (update :extends set/union #{name#}))]
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
