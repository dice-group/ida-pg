(ns librarian.generator.query
  (:require [datascript.core :as d]
            [librarian.helpers.map :as hm]
            [librarian.model.concepts.datatype :as datatype]
            [librarian.model.concepts.typed :as typed]
            [librarian.model.concepts.semantic-type :as semantic-type]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.call-value :as call-value]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.callable :as callable]
            [librarian.model.concepts.result :as result]
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
              [?a ::call-parameter/receives ?x]
              (depends-on ?x ?b)]
            '[(depends-on ?a ?b)
              [?call ::call/result ?a]
              (depends-on ?call ?b)]

            ; does ?a receive the value of ?b:
            '[(receives ?a ?b)
              [(ground ?b) ?a]]
            '[(receives ?a ?b)
              [?a ::call-parameter/receives ?b]]
            '[(receives ?a ?b)
              [?x ::call-parameter/receives ?b]
              (receives ?a ?x)]
            '[(receives ?a ?b)
              [?b ::call-parameter/parameter ?param]
              [?result ::result/receives ?param]
              [?a ::call-result/result ?result]]
            '[(receives ?a ?b)
              [?b ::call-parameter/parameter ?param]
              [?result ::result/receives ?param]
              [?x ::call-result/result ?result]
              (receives ?a ?x)]

            ; does ?a receive the semantic types of ?b:
            '[(receives-semantic ?a ?b)
              [(ground ?b) ?a]]
            '[(receives-semantic ?a ?b)
              [?a ::call-parameter/receives ?b]]
            '[(receives-semantic ?a ?b)
              [?x ::call-parameter/receives ?b]
              (receives-semantic ?a ?x)]
            '[(receives-semantic ?a ?b)
              [?b ::call-parameter/parameter ?param]
              [?result ::result/receives ?param]
              [?a ::call-result/result ?result]]
            '[(receives-semantic ?a ?b)
              [?b ::call-parameter/parameter ?param]
              [?result ::result/receives ?param]
              [?x ::call-result/result ?result]
              (receives-semantic ?a ?x)]
            '[(receives-semantic ?a ?b)
              [?b ::call-parameter/parameter ?param]
              [?result ::result/receives-semantic ?param]
              [?a ::call-result/result ?result]]
            '[(receives-semantic ?a ?b)
              [?b ::call-parameter/parameter ?param]
              [?result ::result/receives-semantic ?param]
              [?x ::call-result/result ?result]
              (receives-semantic ?a ?x)]])

(defn transitive-closure
  [db attr start]
  (persistent! (loop [open (vec start)
                      closure (transient #{})]
                 (let [e (peek open)]
                   (if (or (nil? e) (contains? closure e))
                     closure
                     (recur (into (pop open) (map :v)
                                  (d/datoms db :eavt e attr))
                            (conj! closure e)))))))

(defn typed-compatible?
  [db from to]
  (let [from-types (transitive-closure db ::datatype/extends
                                       (mapv :v (d/datoms db :eavt from ::typed/datatype)))
        to-types (keep (fn [to-type]
                         (let [v (:v to-type)]
                           (when-not (some #(isa? (:v %) ::semantic-type/semantic-type)
                                           (d/datoms db :eavt v :type))
                             v)))
                       (d/datoms db :eavt to ::typed/datatype))]
    (every? #(contains? from-types %) to-types)))

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

(defn compatibly-typed-sources
  ([db flaw]
   (compatibly-typed-sources db flaw nil))
  ([db flaw snippet]
   (let [type-filter '(or (type ?solution ::call-result/call-result)
                          (type ?solution ::call-value/call-value))
         datatype-filter '(typed-compatible ?solution ?flaw)
         deps-filter '(not (depends-on ?solution ?flaw))]
     (d/q {:find '[[?solution ...]]
           :in '[$ % ?flaw ?snippet]
           :where (if snippet
                    ['[?snippet ::snippet/contains ?solution]
                     type-filter datatype-filter deps-filter]
                    [type-filter
                     '(not [_ ::snippet/contains ?solution])
                     datatype-filter deps-filter])}
          db rules flaw snippet))))

(defn types->instances
  ([db]
   (comp (mapcat #(conj (descendants %) %))
         (distinct)
         (mapcat #(d/datoms db :avet :type %))
         (map :e)
         (distinct)))
  ([db types]
   (into [] (types->instances db) types)))

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
