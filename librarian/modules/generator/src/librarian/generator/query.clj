(ns librarian.generator.query
  (:require [datascript.core :as d]
            [librarian.helpers.map :as hm]
            [librarian.helpers.transients :refer [into!]]
            [librarian.model.concepts.datatype :as datatype]
            [librarian.model.concepts.typed :as typed]
            [librarian.model.concepts.semantic-type :as semantic-type]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.parameter :as parameter]
            [librarian.model.concepts.data-receiver :as data-receiver]
            [librarian.model.concepts.constant :as constant]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.callable :as callable]
            [librarian.model.concepts.snippet :as snippet]
            [librarian.model.concepts.named :as named]
            [librarian.model.concepts.namespace :as namespace]
            [librarian.model.concepts.namespaced :as namespaced]
            [librarian.model.concepts.positionable :as positionable]))

(defn transitive-closure
  [db attrs reverse-attrs start]
  (loop [open (transient (vec start))
         closure (transient #{})]
    (let [open-size (count open)]
      (if-some [e (when (not= open-size 0) (nth open (dec open-size)))]
        (recur (-> open
                   (pop!)
                   (into! (comp (mapcat #(d/datoms db :eavt e %))
                                (map :v)
                                (remove closure))
                          attrs)
                   (into! (comp (mapcat #(d/datoms db :avet % e))
                                (map :e)
                                (remove closure))
                          reverse-attrs))
               (conj! closure e))
        (persistent! closure)))))

(defn typed-compatible?
  ([db from to]
   ((typed-compatible? db to) from))
  ([db to]
   (let [to-types (keep (fn [to-type]
                          (let [v (:v to-type)]
                            (when-not (some #(isa? (:v %) ::semantic-type/semantic-type)
                                            (d/datoms db :eavt v :type))
                              v)))
                        (d/datoms db :eavt to ::typed/datatype))]
     (if (seq to-types)
       (fn [from]
         (let [from-types (transitive-closure db [::datatype/extends] nil
                                             (mapv :v (d/datoms db :eavt from ::typed/datatype)))]
           (every? #(contains? from-types %) to-types)))
       (constantly false)))))

(defn dependents
  [db id]
  (transitive-closure db
                      [::call/result]
                      [::call/parameter ::data-receiver/receives]
                      [id]))

(defn depends-on?
  ([db a b]
   ((depends-on? db b) a))
  ([db b]
   (let [deps (dependents db b)]
     (fn [a] (contains? deps a)))))

(defn optional-call-param?
  [db call-param]
  (-> (d/entity db call-param) ::call-parameter/parameter ::parameter/optional some?))

(defn placeholder?
  [db id]
  (:v (first (d/datoms db :aevt :placeholder id)) false))

(defn containing-snippet
  [db id]
  (:e (first (d/datoms db :avet ::snippet/contains id))))

(defn receives?
  [db id]
  (some? (d/datoms db :aevt ::data-receiver/receives id)))

(defn receivers
  [db id]
  (loop [open (transient [id])
         semantic-closure (transient #{id})
         full-closure (transient #{id})]
    (let [open-size (count open)]
      (if-some [e (when (not= open-size 0) (nth open (dec open-size)))]
        (let [in-full-closure (contains? full-closure e)
              semantic-recs (into [] (map :e) (d/datoms db :avet ::data-receiver/receives-semantic e))
              full-recs (into [] (map :e) (d/datoms db :avet ::data-receiver/receives e))
              semantic-closure-cleanup (remove semantic-closure)]
          (recur (-> open
                     (pop!)
                     (into! semantic-closure-cleanup semantic-recs)
                     (into! (if in-full-closure
                              (remove full-closure)
                              semantic-closure-cleanup)
                            full-recs))
                 (-> semantic-closure
                     (into! semantic-recs)
                     (into! full-recs))
                 (if in-full-closure
                   (into! full-closure full-recs)
                   full-closure)))
        {:semantic (persistent! semantic-closure)
         :full (persistent! full-closure)}))))

(defn types
  [db e]
  (->> (d/entity db e)
       ::typed/datatype
       (map (fn [t] {:db/id (:db/id t)
                     :semantic (some #(isa? % ::semantic-type/semantic-type)
                                     (:type t))}))))

(defn semantic-types
  [db e]
  (keep #(when (:semantic %) (:db/id %)) (types db e)))

(defn type-semantics
  [db semantic-type]
  {:key (:v (first (d/datoms db :eavt semantic-type ::semantic-type/key)))
   :value (:v (first (d/datoms db :eavt semantic-type ::semantic-type/value)))})

(defn types->subtypes
  ([]
   (comp (mapcat #(conj (descendants %) %))
         (distinct)))
  ([types]
   (into #{} (mapcat #(conj (descendants %) %)) types)))

(defn types->direct-instances
  ([db]
   (comp (mapcat #(d/datoms db :avet :type %))
         (map :e)
         (distinct)))
  ([db types]
   (into [] (types->direct-instances db) types)))

(defn types->instances
  ([db]
   (comp (types->subtypes)
         (types->direct-instances db)))
  ([db types]
   (into [] (types->instances db) types)))

(defn fillable-call-param?
  [db call-param]
  (-> (d/entity db call-param) ::call-parameter/parameter :placeholder not))

(defn callable
  [db call]
  (:v (first (d/datoms db :eavt call ::call/callable))))

(defn compatibly-typed-sources
  ([db flaw]
   (compatibly-typed-sources db flaw nil))
  ([db flaw snippet]
   (let [datatype-filter (filter (typed-compatible? db flaw))
         deps-filter (remove (depends-on? db flaw))
         source-types (types->subtypes [::call-result/call-result ::constant/constant])]
     (if snippet
       (into []
             (comp (map :v)
                   (filter (fn [s] (some #(source-types (:v %))
                                         (d/datoms db :eavt s :type))))
                   deps-filter datatype-filter)
             (d/datoms db :eavt snippet ::snippet/contains))
       (into []
             (comp (types->direct-instances db)
                   (remove #(d/datoms db :avet ::snippet/contains %))
                   deps-filter datatype-filter)
             source-types)))))

(defn placeholder-matches
  [db placeholder]
  (let [e (d/entity db placeholder)]
    (if (:placeholder e)
      (let [types (:type e)
            supertypes (into #{} (mapcat #(conj (ancestors %) %)) types)
            base-attrs (cond-> []
                         (supertypes ::named/named) (conj ::named/name)
                         (supertypes ::positionable/positionable) (conj ::positionable/position))
            ref-attrs (cond-> []
                        (supertypes ::callable/callable) (into [::callable/parameter
                                                                ::callable/result]))
            revref-attrs (cond-> []
                           (supertypes ::namespaced/namespaced) (conj ::namespace/member))
            base (mapcat #(d/datoms db :eavt placeholder %) base-attrs)
            refs (into []
                       (comp (mapcat #(d/datoms db :eavt placeholder %))
                             (map (fn [datom] [(:a datom) (placeholder-matches db (:v datom))])))
                       ref-attrs)
            revrefs (into []
                          (comp (mapcat #(d/datoms db :avet % placeholder))
                                (map (fn [datom] [(:a datom) (placeholder-matches db (:e datom))])))
                          revref-attrs)
            cands (into []
                        (comp (types->instances db)
                              (remove #(placeholder? db %))
                              (filter #(every? (fn [datom] (d/datoms db :eavt % (:a datom) (:v datom)))
                                               base))
                              (filter (fn [cand]
                                        (every? (fn [[attr cand-refs]]
                                                  (some #(d/datoms db :avet attr cand (:match %)) cand-refs))
                                                revrefs)))
                              (keep (fn [cand]
                                      (hm/update-into {:placeholder placeholder, :match cand}
                                                      (comp (map (fn [[attr cand-refs]]
                                                                   (let [cr (filterv #(d/datoms db :eavt
                                                                                                cand attr
                                                                                                (:match %))
                                                                                    cand-refs)]
                                                                     (when (seq cr) [attr cr]))))
                                                            (hm/all))
                                                      (fnil conj [])
                                                      refs))))
                        types)]
        cands)
      [{:placeholder placeholder, :match placeholder}])))
