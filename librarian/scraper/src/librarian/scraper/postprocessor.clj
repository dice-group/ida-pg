(ns librarian.scraper.postprocessor
  (:require [datascript.core :as d]
            [clojure.spec.alpha :as s]
            [librarian.helpers.spec :as hs]
            [librarian.helpers.transaction :as tx]
            [librarian.helpers.transients :refer [into!]]
            [clojure.tools.logging :as log]))

(defn retract-tempids
  [db ids]
  (mapv #(vector :db.fn/retractAttribute % :tempid) ids))

(defn complete-ids
  [ids]
  (mapv #(vector :db.fn/retractAttribute % :allow-incomplete) ids))

(defn validate-transaction
  [db {:keys [specs] :as ecosystem} ids]
  (let [{:keys [valid invalid completed]}
        (group-by (fn [id]
                    (let [{:keys [type allow-incomplete] :as e} (d/entity db id)
                          specs (keep specs type)]
                      (if (and (seq specs) (s/valid? (apply hs/and specs) e))
                        (if allow-incomplete :completed :valid)
                        (do
                          (log/debug (str "Retract entity " id ".")
                                     type
                                     (if (seq specs)
                                       (s/explain-str (apply hs/and specs) e)
                                       "No specs found."))
                          (if allow-incomplete :incomplete :invalid)))))
                  ids)
        tx (complete-ids completed)]
    (if (empty? invalid)
      tx
      (-> (transient tx)
          (into! (map #(vector :db.fn/retractEntity %) invalid))
          ; revalidate remaining concepts after concept removal
          ; to check whether they are still valid:
          (conj! [:db.fn/call validate-transaction ecosystem (concat valid completed)])
          (persistent!)))))

(defn ecosystem-postprocessing
  [db {:keys [postprocessors]} ids id->tid]
  (keep (fn [id]
          (let [types (:type (d/pull db [:type] id))
                procs (->> types
                           (keep postprocessors)
                           (map #(fn [_] (% db id))))]
            (when (seq procs)
              ; Use calls instead of mapcatting all results for lazy evaluation:
              [:db.fn/call (tx/replace-ids id->tid (apply tx/merge procs))])))
        ids))

(defn postprocess-transactions
  [db ecosystem tids]
  (let [ids (map #(d/entid db [:tempid %]) tids)
        id->tid (zipmap ids tids) ; if tids were unified, the last tid wins
        ids (distinct ids)]
    [[:db.fn/call ecosystem-postprocessing ecosystem ids id->tid]
     [:db.fn/call retract-tempids ids]
     [:db.fn/call validate-transaction ecosystem ids]]))
