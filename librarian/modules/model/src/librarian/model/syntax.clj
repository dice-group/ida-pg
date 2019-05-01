(ns librarian.model.syntax
  (:require [clojure.spec.alpha :as s]
            [clojure.set :as set]
            [datascript.core :as d]
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
  ([paradigm extends]
   (merge-paradigms (conj extends (update paradigm :builtins #(when % (vector %))))))
  ([paradigms]
   (apply map/merge-by-key
          {:concepts merge
           :builtins (comp vec concat)}
          paradigms)))

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
  [{:keys [concepts builtins]}]
  {:concepts concepts
   :builtins builtins
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
  [& ecosystems]
  (->> ecosystems
       (map #(select-keys % [:concepts :builtins]))
       (apply merge-paradigms)
       paradigm->ecosystem))

(defn- instance->tx
  [instance]
  (-> instance meta (:tx [instance]) vec))

(defn instances->tx
  ([root-instances]
   (instances->tx root-instances true))
  ([root-instances check-instances]
   (let [raw-tx (mapcat instance->tx root-instances)
         instances (filter map? raw-tx)
         preproc-tx (mapcat (fn [instance]
                              (let [{:keys [concept]} (meta instance)]
                                (mapcat (fn [[attr processor]]
                                          (when (contains? instance attr)
                                            (processor (get instance attr)
                                                       (:db/id instance))))
                                        (:preprocess concept))))
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
     (when check-instances
       (doseq [instance instances
               :let [spec (-> instance meta :concept :spec)
                     e (d/entity db (-> instance :db/id tempid->id))]
               :when (some? spec)]
         (when-not (s/valid? spec e)
           (throw (Error. (str "Invalid instance: " instance "\n"
                               (s/explain-str spec e)))))))
     (concat tx (map (partial tx/replace-ids-in-tx id->tempid)
                     postproc-tx)))))

(defn instanciate*
  ([concept vals]
   (instanciate* identity concept vals))
  ([ref-map-handler concept vals]
   (let [attr-map (->> concept :attributes keys
                       (map (fn [key] [(keyword (name key)) key]))
                       (into {}))
         tempid (:db/id vals (d/tempid nil))
         [vals tx] (reduce (fn [[vals tx] [k v]]
                             (if (= (get (name k) 0) \_)
                               [vals (cond-> tx
                                       true (conj [:db/add (:db/id v v)
                                                   (keyword (namespace k) (subs (name k) 1))
                                                   tempid])
                                       (map? v) (into (instance->tx v)))]
                               (let [attr (attr-map k k)
                                     attr-desc (get-in concept [:attributes attr])
                                     ref (tx/ref-attr? attr-desc)
                                     many (tx/many-attr? attr-desc)
                                     [val insts]
                                     (cond
                                       (not ref) [v []]
                                       (or (not many) (map? v) (not (coll? v)))
                                       (let [v (if (map? v) (ref-map-handler v) v)]
                                         [(:db/id v v) (if (map? v) [v] [])])
                                       :else
                                       (let [v (map #(if (map? %) (ref-map-handler %) %) v)]
                                         [(mapv #(:db/id % %) v)
                                          (filter map? v)]))]
                                 [(conj vals [attr val]) (into tx (mapcat instance->tx)
                                                               insts)])))
                           [[] #{}] vals)
         instance (into {:db/id tempid
                         :type (:ident concept)}
                        vals)]
     ; Add a datascript transaction for the instance.
     ; The instance itself is part of the transaction and gets a concept reference.
     ; This attached metadata is an implementation detail.
     (with-meta instance {:tx (conj tx (with-meta instance {:concept concept}))}))))

(defn instanciate
  [concept & {:as vals}]
  (instanciate* concept vals))

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
(s/def ::concepts (s/map-of keyword? ::concept))

(s/def ::paradigm-desc (s/and (s/cat :extends (s/? (hs/vec-of ::paradigm))
                                     :paradigm (s/* any?))
                              (s/keys :opt-un [::paradigm])
                              (s/conformer (fn [{:keys [paradigm extends]
                                                 :or {paradigm {}}}]
                                             (merge-paradigms paradigm extends)))))
(s/def ::paradigm (s/and (hs/keys* :opt-un [::concepts ::builtins])))
(s/def ::builtins coll?)

(s/def ::ecosystem-desc (s/and ::paradigm-desc
                               (s/conformer paradigm->ecosystem)))
