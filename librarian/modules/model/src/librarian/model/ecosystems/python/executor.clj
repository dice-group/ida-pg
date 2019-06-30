(ns librarian.model.ecosystems.python.executor
  (:require [clojure.tools.logging :as log]
            [libpython-clj.python :as lp]
            [libpython-clj.python.object :as lpo]
            [libpython-clj.python.protocols :as lpp]
            [librarian.model.ecosystems.python.generator :as pg]))

(defmethod lpp/pyobject-as-jvm :ndarray
  [pyobj]
  (lpo/python->jvm-copy-persistent-vector pyobj))

(defmethod lpp/pyobject->jvm :ndarray
  [pyobj]
  (lpo/python->jvm-copy-persistent-vector pyobj))

(defn executor
  [metadata db]
  (lp/initialize!)
  (let [code (pg/generate (assoc metadata :fn-name "solve") db)
        solve (-> (lp/run-simple-string code)
                  :locals
                  (get "solve"))]
    (log/info (str "Created python executor for:\n" code))
    (fn [& args] @(future (apply solve (map lp/->python args))))))
