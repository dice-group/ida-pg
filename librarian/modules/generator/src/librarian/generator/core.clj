(ns librarian.generator.core
  "Provides the core functionality of the librarian code generator."
  (:require [librarian.generator.generate :as gen]
            [librarian.model.syntax :as msyntax]))

(defn search
  "Takes a scrape and the description of an initial CFG in form of a instance description map, as it is found in scraper configuration files.
   Also takes the maximum number of allowed search steps.
   Returns a solution map if an executable solution could be found within the step limit and `nil` otherwise."
  [scrape init-descs limit]
  (let [init-insts (map #(msyntax/instanciate-with-ecosystem (:ecosystem scrape) %)
                        init-descs)
        tx (msyntax/instances->tx init-insts)
        initial-search-state (gen/initial-search-state scrape tx)
        result (gen/search initial-search-state limit)]
    (when result
      (vary-meta result assoc :scrape scrape))))

(defn solver
  "Takes a search solution and returns a function that executes the found solution when called."
  [solution]
  (let [scrape (-> solution meta :scrape)]
    ((-> scrape :ecosystem :executor) (:meta scrape) (:db solution))))

(defn code
  "Takes a search solution and returns a the found code snippet."
  [solution & args]
  (let [scrape (-> solution meta :scrape)]
    (apply (-> scrape :ecosystem :generate) (:meta scrape) (:db solution) args)))

(def search-solver (comp solver search))
(def search-code (comp code search))
