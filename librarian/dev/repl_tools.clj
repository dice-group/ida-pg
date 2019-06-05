(ns repl-tools
  (:require [proto-repl-charts.graph :as prg]
            [datascript.core :as d]
            [librarian.scraper.io.scrape :refer [create-scrape]]
            [librarian.model.io.scrape :refer [read-scrape]]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.data-receiver :as data-receiver]
            [librarian.model.concepts.call-value :as call-value]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.parameter :as parameter]
            [librarian.model.concepts.namespace :as namespace]
            [librarian.model.concepts.named :as named]
            [librarian.model.concepts.namespaced :as namespaced]
            [librarian.model.concepts.typed :as typed]
            [librarian.model.concepts.basetype :as basetype]
            [librarian.model.concepts.role-type :as role-type]
            [librarian.model.concepts.semantic-type :as semantic-type]
            [librarian.model.concepts.snippet :as snippet]
            [librarian.generator.query :as gq]))

(def show-scrape (comp :db read-scrape create-scrape))
(def shown-state (atom nil))

(defn show-state
  [state & {:keys [show-patterns no-effects]
            :or {show-patterns false, no-effects false}}]
  (let [db (:db state)
        nodes (d/q '[:find ?node ?type
                     :in $ % ?show-patterns
                     :where [(ground [::call/call
                                      ::call-value/call-value
                                      ::call-parameter/call-parameter
                                      ::call-result/call-result])
                             [?type ...]]
                            (type ?node ?type)
                            (or (and [(true? ?show-patterns)] [?node])
                                (and [(false? ?show-patterns)]
                                     (not-join [?node]
                                       [_ ::snippet/contains ?node])))]
                   db gq/rules show-patterns)
        nodes (map (fn [[node type]]
                     {:id node
                      :group type
                      :label
                      (str
                        (case type
                          ::call/call
                          (let [e (::call/callable (d/entity db node))
                                n (or (::namespaced/id e)
                                      [(get-in e [::namespace/_member ::named/name] "?")
                                       (::named/name e "?")])]
                            (clojure.string/join "/" n))
                          ::call-value/call-value
                          (::call-value/value (d/entity db node))
                          ::call-parameter/call-parameter
                          (let [e (d/entity db node)
                                p (::call-parameter/parameter e)]
                            (str (::named/name p "?")
                                 (when (::parameter/optional p) "?")))
                          ::call-result/call-result
                          (get-in (d/entity db node)
                                  [::call-result/result ::named/name]
                                  "?")
                          "?")
                        " (" node ")"
                        "\n<"
                        (->> (::typed/datatype (d/entity db node))
                             (map (fn [datatype]
                                    (condp #(isa? %2 %1) (first (:type datatype))
                                      ::basetype/basetype
                                      (::basetype/id datatype)
                                      ::role-type/role-type
                                      (str "role:" (name (::role-type/id datatype)))
                                      ::semantic-type/semantic-type
                                      (str "s:" (name (::semantic-type/key datatype)) ":"
                                           (name (::semantic-type/value datatype)))
                                      "?")))
                             (clojure.string/join ", "))
                        ">")})
                   nodes)
        receive-edges (->> nodes
                           (filter #(= (:group %) ::call-parameter/call-parameter))
                           (mapcat (fn [{:keys [id]}]
                                     (d/q '[:find ?source ?param
                                            :in $ ?param
                                            :where [?param ::data-receiver/receives ?source]]
                                          db id)))
                           (map (fn [[from to]] {:from from, :to to, :label "flow"})))
        parameter-edges (->> nodes
                           (filter #(= (:group %) ::call-parameter/call-parameter))
                           (mapcat (fn [{:keys [id]}]
                                     (d/q '[:find ?param ?call
                                            :in $ ?param
                                            :where [?call ::call/parameter ?param]]
                                          db id)))
                           (map (fn [[from to]] {:from from, :to to, :label "param"})))
        result-edges (->> nodes
                        (filter #(= (:group %) ::call-result/call-result))
                        (mapcat (fn [{:keys [id]}]
                                  (d/q '[:find ?call ?result
                                         :in $ ?result
                                         :where [?call ::call/result ?result]]
                                       db id)))
                        (map (fn [[from to]] {:from from, :to to, :label "result"})))
        edges (concat receive-edges
                      parameter-edges
                      result-edges)
        edges (if (empty? edges) [{:from -1 :to -2}] edges)]
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

(defn show-search-state
  [search-state & opts]
  (apply show-state (if (:done search-state)
                      (:goal search-state)
                      (-> search-state :queue peek first))
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
