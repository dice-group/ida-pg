(ns librarian.helpers.spec
  (:require [clojure.spec.alpha :as s])
  (:refer-clojure :exclude [and instance?]))

(defn filterwalk
  [f form]
  (if (f form)
    [form]
    (if (seqable? form)
      (mapcat (partial filterwalk f) form)
      [])))

(defmacro entity-keys
  [& {:keys [req opt req-un opt-un gen]}]
  `(let [req-filtered# (filterwalk keyword? ~req)
         req-un-filtered# (filterwalk keyword? ~req-un)
         kw-name# (comp keyword name)
         keys# (concat req-filtered# ~opt
                       (map kw-name# req-un-filtered#)
                       (map kw-name# ~opt-un))]
     (s/and (s/conformer #(with-meta (select-keys % keys#)
                                     {:entity %}))
            (s/keys ~@(when req [:req req])
                    ~@(when opt [:opt opt])
                    ~@(when req-un [:req-un req-un])
                    ~@(when opt-un [:opt-un opt-un])
                    ~@(when gen [:gen gen]))
            (s/conformer (comp :entity meta)))))

(defmacro keys*
  "Like clojure.spec.alpha/keys* but always conforms to and allows maps."
  [& args]
  `(s/and (s/or :map (s/and map? (s/keys ~@args))
                :default (s/keys* ~@args))
          (s/conformer ~(fn [[_ map]] (if (nil? map) {} map)))))

(defn and
  "A function wrapper around clojure.spec.alpha/and."
  [& [first second third & rest :as pred-forms]]
  (case (count pred-forms)
    0 any?
    1 first
    2 (s/and first second)
    3 (s/and first second third)
    (loop [res (s/and first second third)
           [form & forms] rest]
      (if form
        (recur (s/and res form) forms)
        res))))

(defn vec-of
  [pred-form]
  (s/and vector? (s/coll-of pred-form)))

(defn instance?
  [concept]
  (let [parent (if (keyword? concept) concept (:ident concept))]
    (fn [entity]
      (some #(isa? % parent)
            (:type entity)))))
