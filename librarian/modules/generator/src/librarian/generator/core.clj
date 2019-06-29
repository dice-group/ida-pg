(ns librarian.generator.core
  (:require [librarian.generator.generate :as gen]
            [librarian.model.syntax :as msyntax]))

(defn search
  [scrape init-descs limit]
  (let [init-insts (map #(msyntax/instanciate-with-ecosystem (:ecosystem scrape) %)
                        init-descs)
        tx (msyntax/instances->tx init-insts)
        initial-search-state (gen/initial-search-state scrape tx)
        result (gen/search initial-search-state limit)]
    (when result
      (vary-meta result assoc :scrape scrape))))

(defn solver
  [solution]
  (let [scrape (-> solution meta :scrape)]
    ((-> scrape :ecosystem :executor) (:meta scrape) (:db solution))))

(defn code
  [solution & args]
  (let [scrape (-> solution meta :scrape)]
    (apply (-> scrape :ecosystem :generate) (:meta scrape) (:db solution) args)))

(def search-solver (comp solver search))
(def search-code (comp code search))
