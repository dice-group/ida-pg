(ns repl-tools
  (:require [proto-repl-charts.graph :as prg]
            [datascript.core :as d]
            [loom.graph :as g]
            [loom.attr :as ga]
            [librarian.scraper.io.scrape :refer [create-scrape]]
            [librarian.model.io.scrape :refer [read-scrape]]
            [librarian.model.cfg :as cfg]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.constant :as constant]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.parameter :as parameter]
            [librarian.model.concepts.namespace :as namespace]
            [librarian.model.concepts.named :as named]
            [librarian.model.concepts.namespaced :as namespaced]
            [librarian.model.concepts.typed :as typed]
            [librarian.model.concepts.basetype :as basetype]
            [librarian.model.concepts.role-type :as role-type]
            [librarian.model.concepts.semantic-type :as semantic-type]))

(def show-scrape (comp :db read-scrape create-scrape))
(def shown-state (atom nil))

(defn show-state
  [state & {:keys [snippets unused-constants no-effects]
            :or {snippets false, unused-constants false, no-effects false}}]
  (let [db (:db state)
        g (cfg/db->cfg db
                       :snippets snippets
                       :unused-constants unused-constants)
        nodes (keep (fn [node]
                      (let [e (d/entity db node)
                            datatypes (::typed/datatype e)
                            type (ga/attr g node :type)]
                        {:id node
                         :group type
                         :label
                         (str
                           (case type
                             ::call/call
                             (let [e (::call/callable e)
                                   n (or (::namespaced/id e)
                                         [(get-in e [::namespace/_member ::named/name] "?")
                                          (::named/name e "?")])]
                               (clojure.string/join "/" n))
                             ::constant/constant
                             (::constant/value e)
                             ::call-parameter/call-parameter
                             (let [p (::call-parameter/parameter e)]
                               (str (::named/name p "?")
                                    (when (::parameter/optional p) "?")))
                             ::call-result/call-result
                             (get-in e [::call-result/result ::named/name] "?")
                             "?")
                           " (" node ")"
                           (when (seq datatypes) "\n<")
                           (->> datatypes
                                (keep (fn [datatype]
                                        (when (not= node (:db/id datatype))
                                          (condp #(isa? %2 %1) (first (:type datatype))
                                            ::basetype/basetype
                                            (::basetype/id datatype)
                                            ::role-type/role-type
                                            (str "role:" (name (::role-type/id datatype)))
                                            ::semantic-type/semantic-type
                                            (let [val (name (::semantic-type/value datatype))]
                                              (str "s:" (name (::semantic-type/key datatype)) ":"
                                                   (if (> (count val) 7)
                                                     (str (subs val 0 4) "...")
                                                     val)))
                                            ::constant/constant
                                            (str "c:" (:db/id datatype))
                                            "?"))))
                                (clojure.string/join ", "))
                           (when (seq datatypes) ">"))}))
                    (g/nodes g))
        edges (g/edges g)
        edges (if (empty? edges)
                [{:from -1 :to -2}]
                (map (fn [[from to]]
                       {:from from :to to
                        :label (ga/attr g from to :type)})
                     edges))]
    (when-not no-effects (reset! shown-state state))
    (prg/graph "Control Flow State"
               {:nodes nodes
                :edges edges}
               {:edges {:arrows "to"}
                :nodes {:shape "box"
                        :labelHighlightBold false}
                :physics {:hierarchicalRepulsion {:nodeDistance 60
                                                  :springLength 80}}
                :layout {:hierarchical {:enabled true
                                        :direction "UD"
                                        :sortMethod "directed"
                                        :levelSeparation 80}}})))

(defn search-state->next-state
  [search-state]
  (if (:done search-state)
    (:goal search-state)
    (-> search-state :queue peek first)))

(defn show-search-state
  [search-state & opts]
  (apply show-state (search-state->next-state search-state)
         opts))

(defn state-past
  [state predecessor-idx]
  (nth (iterate :predecessor state)
       predecessor-idx))

(defn show-state-past
  [state predecessor-idx & opts]
  (apply show-state
         (state-past state predecessor-idx)
         opts))

(defn last-state
  ([]
   @shown-state)
  ([predecessor-idx]
   (state-past @shown-state predecessor-idx)))

(defn show-last-state
  ([]
   (show-last-state 0))
  ([predecessor-idx]
   (show-state-past @shown-state predecessor-idx
                    :no-effects true)))
