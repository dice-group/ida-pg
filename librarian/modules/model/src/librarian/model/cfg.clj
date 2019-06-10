(ns librarian.model.cfg
  (:require [datascript.core :as d]
            [loom.graph :as g]
            [loom.attr :as ga]
            [librarian.model.db :as mdb]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.constant :as constant]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.snippet :as snippet]
            [librarian.model.concepts.data-receiver :as data-receiver]))

(defn add-attrs
  [g ne-attr-pairs]
  (transduce (mapcat (fn [[n attrs]]
                       (eduction (map (fn [[k v]] [n k v])) attrs)))
             (completing (fn [g [n k v]] (ga/add-attr g n k v)))
             g ne-attr-pairs))

(defn db->cfg
  [db & {:keys [snippets unused-constants]
         :or {snippets false, unused-constants false}}]
  (let [nodes (d/q {:find '[?node ?type]
                    :in '[$ % ?snippets ?unused-constants]
                    :where (cond-> '[[(ground [::call/call
                                               ::constant/constant
                                               ::call-parameter/call-parameter
                                               ::call-result/call-result])
                                      [?type ...]]
                                     (type ?node ?type)]
                             (not snippets)
                             (conj '(not-join [?node] [_ ::snippet/contains ?node]))
                             (not unused-constants)
                             (conj '(or-join [?node ?type]
                                      (and [(not= ?type ::constant/constant)]
                                           [?node])
                                      (and [(= ?type ::constant/constant)]
                                           [_ ::data-receiver/receives ?node]))))}
                   db mdb/rules snippets unused-constants)
        receive-edges (eduction (comp (filter #(= (second %) ::call-parameter/call-parameter))
                                      (mapcat (fn [[id]]
                                                (d/q '[:find ?source ?param
                                                       :in $ ?param
                                                       :where [?param ::data-receiver/receives ?source]]
                                                     db id)))
                                      (map (fn [e] [e {:type :flow}])))
                                nodes)
        parameter-edges (eduction (comp (filter #(= (second %) ::call-parameter/call-parameter))
                                        (mapcat (fn [[id]]
                                                  (d/q '[:find ?param ?call
                                                         :in $ ?param
                                                         :where [?call ::call/parameter ?param]]
                                                       db id)))
                                        (map (fn [e] [e {:type :param}])))
                                  nodes)
        result-edges (eduction (comp (filter #(= (second %) ::call-result/call-result))
                                     (mapcat (fn [[id]]
                                               (d/q '[:find ?call ?result
                                                      :in $ ?result
                                                      :where [?call ::call/result ?result]]
                                                    db id)))
                                     (map (fn [e] [e {:type :result}])))
                               nodes)
        edges (into [] cat [receive-edges parameter-edges result-edges])
        nodes (into [] (map (fn [[id type]] [id {:type type}]))
                    nodes)]
    (-> (g/digraph)
        (g/add-nodes* (map first nodes))
        (add-attrs nodes)
        (g/add-edges* (map first edges))
        (add-attrs edges))))
