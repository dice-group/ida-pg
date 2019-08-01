(ns librarian.model.ecosystems.python.executor
  "Interop layer to execute Python code from Java."
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
      (contains? o "__set__") (set (o "__set__"))
      (contains? o "__ndarray__") (o "__ndarray__")
      (contains? o "__decimal__") (Double/parseDouble (o "__decimal__"))
      (contains? o "__fraction__") (/ (o "numerator") (o "denominator"))
      :else o)
    o))

(defmethod executor :shell
  [metadata db]
  (let [code (pg/generate (assoc metadata :fn-name "solve") db)
        _ (log/info (str "Created python executor for:\n" code))
        f (fn [& args]
            (let [arg-str (str/join ", " (map pg/literal args))
                  call-code
                  (str code "\n"
                       "import json_tricks\n"
                       "print(json_tricks.dumps(solve(" arg-str ")))\n")
                  {:keys [exit out err]} (sh/sh "python3" :in call-code)]
              (if (zero? exit)
                (walk/postwalk python->clj (json/read-str out))
                (throw (ex-info "Error executing Python code."
                                {:exit exit
                                 :err err})))))]
    (vary-meta f assoc :code code)))
