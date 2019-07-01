(ns librarian.model.ecosystems.python.executor
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str]
            [clojure.java.shell :as sh]
            [clojure.walk :as walk]
            [clojure.data.json :as json]
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

(defmulti executor
  (fn [meta _] (:executor meta))
  :default :shell)

(defmethod executor :native
  [metadata db]
  (lp/initialize!)
  (let [code (pg/generate (assoc metadata :fn-name "solve") db)
        solve (-> (lp/run-simple-string code)
                  :locals
                  (get "solve"))]
    (log/info (str "Created python executor for:\n" code))
    (fn [& args] @(future (apply solve (map lp/->python args))))))

(defn python->clj
  [o]
  (if (map? o)
    (cond
      (contains? o "py/set") (set (o "py/set"))
      (contains? o "py/tuple") (o "py/tuple")
      (= (o "py/object") "numpy.ndarray") (o "values")
      :else o)
    o))

(defmethod executor :shell
  [metadata db]
  (let [code (pg/generate (assoc metadata :fn-name "solve") db)]
    (log/info (str "Created python executor for:\n" code))
    (fn [& args]
      (let [arg-str (str/join ", " (map pg/literal args))
            call-code
            (str code "\n"
                 "import jsonpickle\n"
                 "import jsonpickle.ext.numpy as jsonpickle_numpy\n"
                 "jsonpickle_numpy.register_handlers()\n"
                 "print(jsonpickle.encode(solve(" arg-str ")))\n")
            {:keys [exit out err]} (sh/sh "python3" :in call-code)]
        (if (zero? exit)
          (walk/postwalk python->clj (json/read-str out))
          (throw (ex-info "Error executing Python code."
                          {:exit exit
                           :err err})))))))
