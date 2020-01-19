(ns librarian.generator.actions.snippet
  "Definition of the class of snippet insertion actions."
  (:require [datascript.core :as d]
            [librarian.helpers.transaction :as tx]
            [librarian.generator.query :as gq]
            [librarian.model.concepts.namespace :as namespace]
            [librarian.model.concepts.snippet :as snippet]))

(defn snippet->action
  [db snippet]
  (let [tx (tx/clone-entities db (map :v (d/datoms db :eavt snippet ::snippet/contains))
                              [::namespace/member])]
    {:type ::snippet
     :weight 1/2
     :add true
     :id snippet
     :tx tx}))

(defn snippet-actions
  [{:keys [db]} flaw]
  (sequence (comp (map :e)
                  (remove #(empty? (gq/compatibly-typed-sources db flaw %)))
                  (map #(snippet->action db %)))
            (d/datoms db :avet :type ::snippet/snippet)))
