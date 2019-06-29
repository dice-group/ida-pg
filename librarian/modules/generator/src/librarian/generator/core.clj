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
  ((-> solution meta :scrape :ecosystem :executor) solution))

(def search-solver (comp solver search))
