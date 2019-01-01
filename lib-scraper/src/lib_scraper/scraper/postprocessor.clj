(ns lib-scraper.scraper.postprocessor
  (:require [datascript.core :as d]
            [clojure.spec.alpha :as s]))

(defn retract-tempids
  [db ids]
  (mapv #(vector :db.fn/retractAttribute % :tempid) ids))

(defn validate-transaction
  [db ids]
  (let [{:keys [valid invalid]} (group-by (fn [id]
                                            (let [e (d/entity db id)]
                                              (if (s/valid? (:type e) e)
                                                :valid :invalid)))
                                          ids)]
    (if (empty? invalid)
      []
      (conj (mapv #(vector :db.fn/retractEntity %) invalid)
            ; revalidate remaining concepts after concept removal
            ; to check whether they are still valid:
            [:db.fn/call validate-transaction valid]))))

(defn postprocess-transactions
  [db tids]
  (let [ids (map #(d/entid db [:tempid %]) tids)]
    [[:db.fn/call retract-tempids ids]
     [:db.fn/call validate-transaction ids]]))
