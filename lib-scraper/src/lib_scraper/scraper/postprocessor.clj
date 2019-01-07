(ns lib-scraper.scraper.postprocessor
  (:require [datascript.core :as d]
            [clojure.spec.alpha :as s]
            [lib-scraper.helpers.spec :as hs]
            [lib-scraper.helpers.transaction :as tx]))

(defn retract-tempids
  [db ids]
  (mapv #(vector :db.fn/retractAttribute % :tempid) ids))

(defn validate-transaction
  [db {:keys [specs] :as ecosystem} ids]
  (let [{:keys [valid invalid]}
        (group-by (fn [id]
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
  [db {:keys [postprocessors]} ids tids tid->id]
  (let [results (into {} (for [id ids
                               :let [types (:type (d/pull db [:type] id))
                                     procs (keep postprocessors types)]
                               :when (seq procs)
                               :let [proc (apply tx/merge procs)]]
                           [id (delay (proc db id))]))]
    (keep (fn [tid]
            (let [id (tid->id tid)
                  result (results id)]
              (if result
                [:db.fn/call (fn [_] (tx/replace-id id tid @result))])))
          tids)))

(defn postprocess-transactions
  [db ecosystem tids]
  (let [ids (map #(d/entid db [:tempid %]) tids)
        tid->id (zipmap tids ids)
        ids (distinct ids)]
    [[:db.fn/call retract-tempids ids]
     [:db.fn/call ecosystem-postprocessing ecosystem ids tids tid->id]
     [:db.fn/call validate-transaction ecosystem ids]]))
