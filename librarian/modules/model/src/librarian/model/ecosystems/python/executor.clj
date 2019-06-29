(ns librarian.model.ecosystems.python.executor
  (:require [libpython-clj.python :as lp]
            [librarian.model.ecosystems.python.generator :as pg]))

(defn executor
  [metadata db]
  (lp/initialize!)
  (let [code (pg/generate (assoc metadata :fn-name "solve") db)
        solve (-> (lp/run-simple-string code)
                  :locals
                  lp/as-jvm
                  (get "solve"))]
    #(apply solve (map lp/->python %&))))
