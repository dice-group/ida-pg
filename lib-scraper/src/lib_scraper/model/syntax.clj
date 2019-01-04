(ns lib-scraper.model.syntax
  (:require [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.helpers.map :as map]
            [lib-scraper.helpers.transaction :as tx]
            [lib-scraper.model.common :as common]))

(defn merge-concepts
  [concept extends]
  (let [merged (apply map/merge-by-key
                      {:attributes merge
                       :spec hs/and
                       :postprocess tx/merge}
                      (conj extends concept))]
    (if (contains? merged :spec)
      merged
      (assoc merged :spec any?))))

(defn merge-paradigms
  [paradigm extends]
  (apply merge-with merge (conj extends paradigm)))

(defn no-attribute-collisions?
  [{:keys [concept extends]}]
  (apply distinct? (map name (mapcat (comp keys :attributes)
                                     (conj extends concept)))))

(s/def ::concept (hs/keys* :opt-un [::attributes ::spec ::postprocess]))
(s/def ::attributes (s/every-kv keyword? any?))
(s/def ::postprocess fn?)
(s/def ::concept-desc (s/and (s/cat :extends (s/? (hs/vec-of ::concept))
                                    :concept (s/* any?))
                             (s/keys :req-un [::concept])
                             no-attribute-collisions?
                             (s/conformer (fn [{:keys [concept extends]}]
                                            (merge-concepts concept extends)))))
(s/def ::concept-overlay
       (s/and (s/or :overlay (s/keys :req-un [::concept]
                                     :opt-un [::spec ::postprocess])
                    :concept ::concept)
              (s/conformer (fn [[type {:keys [concept spec postprocess] :as m}]]
                             (case type
                               :concept m
                               :overlay (cond-> (or concept {})
                                          spec (assoc :spec spec)
                                          postprocess (assoc :postprocess postprocess)))))))
(s/def ::concept-overlays (s/and (hs/keys*) (s/map-of keyword? ::concept-overlay)))

(s/def ::paradigm-desc (s/and (s/cat :extends (s/? (hs/vec-of ::paradigm))
                                     :paradigm (s/* any?))
                              (s/keys :req-un [::paradigm])
                              (s/conformer (fn [{:keys [paradigm extends]}]
                                             (merge-paradigms paradigm extends)))))
(s/def ::paradigm ::concept-overlays)

(s/def ::ecosystem-desc
       (s/and ::paradigm-desc
              (s/conformer (fn [m]
                             {:aliases (into {} (for [[c {:keys [attributes]}] m
                                                      :let [cns (namespace c)
                                                            cns (str (if cns (str cns "."))
                                                                     (name c))]
                                                      attribute (keys attributes)]
                                                  [(keyword cns (name attribute)) attribute]))
                              :attributes (reduce-kv (fn [a _ {:keys [attributes]}]
                                                       (into a attributes))
                                                     common/attributes m)
                              :specs (map/map-kv :spec m)
                              :postprocessors (map/keep-kv :postprocess m)}))))

(defmacro defconcept
  [name & concept]
  `(let [concept# ~(vec concept)
         conformed# (s/conform ::concept-desc concept#)]
     (if (s/invalid? conformed#)
       (throw (Exception. (str "Invalid concept definition.\n"
                               (s/explain-str ::concept-desc concept#))))
       (def ~name conformed#))))

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
