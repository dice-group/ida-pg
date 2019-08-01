(ns librarian.generator.query
  "Implementation of common database queries needed to access and extend the CFG encoded in a database."
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
            [librarian.model.concepts.result :as result]
            [librarian.model.concepts.snippet :as snippet]
            [librarian.model.concepts.named :as named]
            [librarian.model.concepts.namespace :as namespace]
            [librarian.model.concepts.namespaced :as namespaced]
            [librarian.model.concepts.positionable :as positionable]))

(defn transitive-closure
  "Computes the transitive closure for a collection of `start` ids and traversable attributes `attrs` and `reverse-attrs`."
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
  "Returns whether a value would be permitted to flow from control flow node `from` to control flow node `to`.
   Supports partial application."
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
  "Returns all control flow nodes that are in some way dependent on the control flow node associated to `id`."
  [db id]
  (transitive-closure db
                      [::call/result]
                      [::call/parameter ::data-receiver/receives]
                      [id]))

(defn depends-on?
  "Returns whether the control flow node with id `a` is a dependent of the node with id `b`."
  ([db a b]
   ((depends-on? db b) a))
  ([db b]
   (let [deps (dependents db b)]
     (fn [a] (contains? deps a)))))

(defn optional-call-param?
  "Returns whether the given call-parameter instance is optional."
  [db call-param]
  (-> (d/entity db call-param) ::call-parameter/parameter ::parameter/optional some?))

(defn placeholder?
  "Returns whether the given control flow node is fully specified or if it still needs to be concretized to a specific instance (e.g. some callable)."
  [db id]
  (:v (first (d/datoms db :aevt :placeholder id)) false))

(defn containing-snippet
  "Returns the id of the snippet that contains the control flow node with the given `id`."
  [db id]
  (:e (first (d/datoms db :avet ::snippet/contains id))))

(defn receives?
  "Returns whether the given control flow node receives some value."
  [db id]
  (some? (d/datoms db :aevt ::data-receiver/receives id)))

(defn receivers
  "Returns a map with keys `:semantic` and `:full`.
   Semantic receivers directly or indirectly inherit the semantic types of the control flow node with the given `id`.
   Full receivers inherit all the datatypes of `id`.

   Example:
   A parameter of type object receiving an integer will then also be of type integer.
   A string value that was typecast to an integer however does not propagate its programmatic type to the cast value but it propagates its semantic type (e.g. the unit of measurement it is using)."
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
  "Returns a collection of the datatypes of the concept with id `e`.
   Each datatype is represented by a map with keys `[:db/id :semantic]`.
   `:semantic` is a boolean representing whether the datatype is a semantic type or not."
  [db e]
  (->> (d/entity db e)
       ::typed/datatype
       (map (fn [t] {:db/id (:db/id t)
                     :semantic (some #(isa? % ::semantic-type/semantic-type)
                                     (:type t))}))))

(defn semantic-types
  "Like `types` but only returns the ids of the semantic types of `e`."
  [db e]
  (keep #(when (:semantic %) (:db/id %)) (types db e)))

(defn type-semantics
  "Returns the semantic information associated to the semantic type with id `semantic-type`."
  [db semantic-type]
  {:key (:v (first (d/datoms db :eavt semantic-type ::semantic-type/key)))
   :value (:v (first (d/datoms db :eavt semantic-type ::semantic-type/value)))})

(defn types->subtypes
  "Returns the identifier keywords of all direct and indirect subconcepts of the given concepts `types`.
   `types` is a collection of concept identifier keywords."
  ([]
   (comp (mapcat #(conj (descendants %) %))
         (distinct)))
  ([types]
   (into #{} (mapcat #(conj (descendants %) %)) types)))

(defn types->direct-instances
  "Returns the ids of direct instances of the given concepts `types`.
   `types` is a collection of concept identifier keywords."
  ([db]
   (comp (mapcat #(d/datoms db :avet :type %))
         (map :e)
         (distinct)))
  ([db types]
   (into [] (types->direct-instances db) types)))

(defn types->instances
  "Like `types->direct-instances` but also returns instances of subtypes of the provided types."
  ([db]
   (comp (types->subtypes)
         (types->direct-instances db)))
  ([db types]
   (into [] (types->instances db) types)))

(defn fillable-call-param?
  "Returns whether the call-parameter with id `call-param` could potentially receive a value.
   In particular this will return `false` for call-parameters of placeholder parameters, since their datatype might not be fully known which might make premature value propagations invalid in the future."
  [db call-param]
  (-> (d/entity db call-param) ::call-parameter/parameter :placeholder not))

(defn callable
  "Returns the id of the callable for the call with id `call`."
  [db call]
  (:v (first (d/datoms db :eavt call ::call/callable))))

(defn compatibly-typed-sources
  "Returns the ids of all control flow nodes that could potentially flow their value into the control flow node with id `flaw`.
   Optionally the search for such sources can be restricted to control flow nodes that are part of a given snippet with id `snippet`."
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

(defn compatibly-typed-callables
  "Returns the ids of all callables that return a value that could potentially flow into the control flow node with id `flaw`."
  [db flaw]
  (into #{}
        (comp (types->direct-instances db)
              (filter (typed-compatible? db flaw))
              (keep #(:e (first (d/datoms db :avet ::callable/result %)))))
        (types->subtypes [::result/result])))

(defn placeholder-matches
  "Takes the id of some placeholder.
   Returns a collection of potential completions/matches for the placeholder.
   Each match is a map with keys `[:placeholder :match]`.
   `:placeholder` is the id of the placeholder that should be replaced and `:match` the id of the concept it should be replaced with.
   Since some placeholder replacements might entail the replacement of other placeholders that are associated to the replaced placeholder, the match map might additionally contain other keys (e.g. a match for a callable placeholder might have a `::callable/parameter` entry).
   Each of those additional keys is a reference attribute of the replaced placeholder.
   They are associated to a collection of matches for referenced placeholder concepts resulting in a recursive placeholder replacement tree.

   Example result for a given placeholder callable with id `1`:
   ```
   [{:placeholder 1
     :match 101
     ::callable/parameter [{:placeholder 2, :match 102}
                           {:placeholder 2, :match 103}]
     ::callable/result [{:placeholder 3, :match 104}]}
    {:placeholder 1
     :match 201
     ::callable/parameter [{:placeholder 2, :match 202}]
     ::callable/result [{:placeholder 3, :match 203}]}]
   ```
   Two candidate callables were found to replace the given placeholder.
   Both candidate matches would require the placeholder parameter and result of the placeholder callable to also be replaced.
   For the first candidate match, two potential replacements for the placeholder parameter were found.
   This matching tree encodes a total of three different replacements that might be performed."
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
                        (comp (types->instances db) ; all instances of placeholder's type
                              (remove #(placeholder? db %)) ; only non-placeholder instances
                              (filter #(every? (fn [datom] (d/datoms db :eavt % (:a datom) (:v datom)))
                                               base)) ; only instances with same base attributes
                              (filter (fn [cand]
                                        (every? (fn [[attr cand-refs]]
                                                  (some #(d/datoms db :avet attr cand (:match %)) cand-refs))
                                                revrefs))) ; only instances from matched namespaces
                              ; only instances with matched params and results:
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
