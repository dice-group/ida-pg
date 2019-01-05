(ns lib-scraper.scraper.postprocessor
  (:require [datascript.core :as d]
            [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]))

(defn retract-tempids
  [db ids]
  (mapv #(vector :db.fn/retractAttribute % :tempid) ids))

(defn validate-transaction
  [db {:keys [specs] :as ecosystem} ids]
  (let [{:keys [valid invalid]} (group-by (fn [id]
                                            (let [{:keys [type] :as e} (d/entity db id)
                                                  specs (keep specs type)]
                                              (if (and (seq specs)
                                                       (s/valid? (apply hs/and specs) e))
                                                :valid :invalid)))
                                          ids)]
    (if (empty? invalid)
      []
      (conj (mapv #(vector :db.fn/retractEntity %) invalid)
            ; revalidate remaining concepts after concept removal
            ; to check whether they are still valid:
            [:db.fn/call validate-transaction ecosystem valid]))))

(defn ecosystem-postprocessing
  [db {:keys [postprocessors]} ids]
  (keep (fn [id]
          (when-let [processor (some-> (d/entity db id) :type postprocessors)]
            [:db.fn/call processor id]))
        ids))

(defn postprocess-transactions
  [db ecosystem tids]
  (let [ids (map #(d/entid db [:tempid %]) tids)]
    [[:db.fn/call retract-tempids ids]
     [:db.fn/call ecosystem-postprocessing ecosystem ids]
     [:db.fn/call validate-transaction ecosystem ids]]))
