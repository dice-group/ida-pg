(ns lib-scraper.helpers.spec
  (:require [clojure.spec.alpha :as s]))

(def ^:private keyword-name (comp keyword name))

(defn filterwalk
  [f form]
  (if (f form)
    [form]
    (if (seqable? form)
      (mapcat (partial filterwalk f) form)
      [])))

(defmacro entity-keys
  [& {:keys [req opt req-un opt-un gen]}]
  (let [req (filterwalk keyword? req)
        req-un (filterwalk keyword? req-un)
        selected-keys (concat req opt
                              (map keyword-name req-un)
                              (map keyword-name opt-un))]
    `(s/and (s/conformer ~#(select-keys % selected-keys))
            (s/keys ~@(when req [:req req])
                    ~@(when opt [:opt opt])
                    ~@(when req-un [:req-un req-un])
                    ~@(when opt-un [:opt-un opt-un])
                    ~@(when gen [:gen gen])))))
