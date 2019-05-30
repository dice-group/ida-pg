(ns librarian.generator.actions.param-remove
  (:require [datascript.core :as d]
            [librarian.model.concepts.parameter :as parameter]
            [librarian.model.concepts.call-parameter :as call-parameter]))

(defn param-remove-actions
  [{:keys [db]} flaw]
  (when (-> (d/entity db flaw) ::call-parameter/parameter ::parameter/optional)
    [{:cost 1
      :tx [[:db.fn/retractEntity flaw]]}]))
