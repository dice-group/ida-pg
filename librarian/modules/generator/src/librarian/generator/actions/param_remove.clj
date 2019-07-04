(ns librarian.generator.actions.param-remove
  (:require [librarian.generator.query :as gq]
            [librarian.generator.cost :as gc]
            [librarian.generator.commutation :as gcomm]))

(defn param-remove-actions
  [{:keys [db]} flaw]
  (when (gq/optional-call-param? db flaw)
    [{:type ::param-remove
      :weight gc/param-remove-weight
      :tx [[:db.fn/retractEntity flaw]]
      :remove [flaw]
      :commute-id flaw}]))

; define parameter removal as a complete commutative action:
(derive ::param-remove ::gcomm/complete)
