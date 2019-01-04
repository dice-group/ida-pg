(ns lib-scraper.helpers.spec
  (:require [clojure.spec.alpha :as s])
  (:refer-clojure :exclude [and]))

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
  (let [req-filtered (filterwalk keyword? req)
        req-un-filtered (filterwalk keyword? req-un)
        keys (concat req-filtered opt
                     (map keyword-name req-un-filtered)
                     (map keyword-name opt-un))]
    `(s/and (s/conformer ~#(with-meta (select-keys % keys)
                                      {:entity %}))
            (s/keys ~@(when req [:req req])
                    ~@(when opt [:opt opt])
                    ~@(when req-un [:req-un req-un])
                    ~@(when opt-un [:opt-un opt-un])
                    ~@(when gen [:gen gen]))
            (s/conformer ~(comp :entity meta)))))

(defmacro keys*
  "Like clojure.spec.alpha/keys* but always conforms to and allows maps."
  [& args]
  `(s/and (s/or :map (s/and map? (s/keys ~@args))
                :default (s/keys* ~@args))
          (s/conformer ~(fn [[_ map]] (if (nil? map) {} map)))))

(defn and
  "A function wrapper around clojure.spec.alpha/and."
  [& [first second third :as pred-forms]]
  (case (count pred-forms)
    0 any?
    1 first
    2 (s/and first second)
    3 (s/and first second third)
    (loop [res (first pred-forms)
           [form & forms] (rest pred-forms)]
      (if form
        (recur (s/and res form) forms)
        res))))

(defn vec-of
  [pred-form]
  (s/and vector? (s/coll-of pred-form)))
