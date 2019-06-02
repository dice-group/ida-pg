(ns librarian.generator.actions.param-remove
  (:require [librarian.generator.query :as gq]))

(defn param-remove-actions
  [{:keys [db]} flaw]
  (when (gq/optional-call-param? db flaw)
    [{:type :param-remove
      :weight 1
      :tx [[:db.fn/retractEntity flaw]]
      :remove [flaw]}]))
