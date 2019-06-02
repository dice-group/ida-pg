(ns librarian.generator.query
  (:require [datascript.core :as d]
            [clojure.core.memoize :as memo]
            [librarian.helpers.map :as hm]
            [librarian.helpers.transients :refer [into!]]
            [librarian.model.concepts.datatype :as datatype]
            [librarian.model.concepts.typed :as typed]
            [librarian.model.concepts.semantic-type :as semantic-type]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.parameter :as parameter]
            [librarian.model.concepts.data-receiver :as data-receiver]
            [librarian.model.concepts.call-value :as call-value]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.callable :as callable]
            [librarian.model.concepts.snippet :as snippet]
            [librarian.model.concepts.named :as named]
            [librarian.model.concepts.namespace :as namespace]
            [librarian.model.concepts.namespaced :as namespaced]
            [librarian.model.concepts.positionable :as positionable]))

(def rules [; is ?c a concept of type ?type:
            '[(type ?c ?type)
              [?c :type ?type]]
            '[(type ?c ?type)
              [?c :type ?t]
              (subtype ?t ?type)]

            ; is ?parent a superconcept of ?child:
            '[(subtype ?child ?parent)
              [(clojure.core/isa? ?child ?parent)]]

            ; does the datatype concept ?child extend the datatype concept ?parent:
            '[(extends ?child ?parent)
              [(ground ?child) ?parent]]
            '[(extends ?child ?parent)
              [?child ::datatype/extends ?parent]]
            '[(extends ?child ?parent)
              [?child ::datatype/extends ?p]
              (extends ?p ?parent)]

            ; can values of typed concept ?from be used as values of typed concept ?to:
            ['(typed-compatible ?from ?to)
             `[(typed-compatible? ~'$ ~'?from ~'?to)]]

            ; does ?a depend on ?b:
            '[(depends-on ?a ?b)
              [(ground ?a) ?b]]
            '[(depends-on ?a ?b)
              [?a ::call/parameter ?param]
              (depends-on ?param ?b)]
            '[(depends-on ?a ?b)
              [?a ::data-receiver/receives ?x]
              (depends-on ?x ?b)]
            '[(depends-on ?a ?b)
              [?call ::call/result ?a]
              (depends-on ?call ?b)]

            ; does ?a receive the value of ?b:
            '[(receives ?a ?b)
              [(ground ?b) ?a]]
            '[(receives ?a ?b)
              [?a ::data-receiver/receives ?b]]
            '[(receives ?a ?b)
              [?x ::data-receiver/receives ?b]
              (receives ?a ?x)]

            ; does ?a receive the semantic types of ?b:
            '[(receives-semantic ?a ?b)
              [(ground ?b) ?a]]
            '[(receives-semantic ?a ?b)
              [?a ::data-receiver/receives ?b]]
            '[(receives-semantic ?a ?b)
              [?x ::data-receiver/receives ?b]
              (receives-semantic ?a ?x)]
            '[(receives-semantic ?a ?b)
              [?a ::data-receiver/receives-semantic ?b]]
            '[(receives-semantic ?a ?b)
              [?x ::data-receiver/receives-semantic ?b]
              (receives-semantic ?a ?x)]])

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

(defn compatibly-typed-sources
  ([db flaw]
   (compatibly-typed-sources db flaw nil))
  ([db flaw snippet]
   (let [datatype-filter (filter (typed-compatible? db flaw))
         deps-filter (remove (depends-on? db flaw))
         source-types (types->subtypes [::call-result/call-result ::call-value/call-value])]
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

(defn ^{::memo/args-fn second} placeholder-matches
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
                              (remove #(:v (first (d/datoms db :aevt :placeholder %))))
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
