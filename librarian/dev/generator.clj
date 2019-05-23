(ns generator
  (:require [librarian.generator.core :as gen]
            [librarian.model.io.scrape :as scrape]
            [repl-tools :as rt]))

(defn gen-test
  ([] (gen-test "libs/scikit-learn-cluster"))
  ([ds]
   (let [scrape (scrape/read-scrape ds)
         search-state (gen/initial-search-state scrape [:dataset] [:labels])
         succs (iterate gen/continue-search search-state)
         succs (take 6 succs)]
     (time (doall succs))
     (time (rt/show-search-state (or (some #(when (:goal %) %) succs)
                                     (last succs)
                                  :show-patterns false))))))
