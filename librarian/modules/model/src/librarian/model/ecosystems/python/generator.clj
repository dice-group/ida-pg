(ns librarian.model.ecosystems.python.generator
  (:require [clojure.string :as str]
            [datascript.core :as d]
            [loom.alg :as alg]
            [loom.attr :as ga]
            [librarian.model.cfg :as cfg]
            [librarian.model.concepts.constant :as constant]
            [librarian.model.concepts.call :as call]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.data-receiver :as data-receiver]
            [librarian.model.concepts.positionable :as positionable]
            [librarian.model.concepts.namespaced :as namespaced]
            [librarian.model.concepts.named :as named]))

(defn vname
  [id]
  (str "v" id))

(defn literal
  [val]
  (cond
    (string? val)
    (str "\""
         (-> val
             (str/replace #"\\" "\\\\")
             (str/replace #"\"" "\\\"")
             (str/replace #"\r" "\\r")
             (str/replace #"\n" "\\n")
             (str/replace #"\t" "\\t"))
         "\"")
    (number? val) (str val)
    (boolean? val) (if val "True" "False")
    (nil? val) "None"
    (coll? val)
    (str "[" (str/join ", " (map literal val)) "]")))

(defmulti line
  (fn [db g id] (ga/attr g id :type)))

(defmethod line ::constant/constant
  [db g id]
  (let [val (::constant/value (d/entity db id))]
    {:line (str (vname id) " = " (literal val))}))

(defmethod line ::call-parameter/call-parameter
  [db g id]
  (let [e (d/entity db id)]
    (if-not (::call-parameter/parameter e)
      {:output (vname (:db/id (::data-receiver/receives e)))
       :position (::positionable/position e)})))

(defmethod line ::call-result/call-result
  [db g id]
  (let [e (d/entity db id)
        call (::call/_result e)]
    (if call
      (when (and (::data-receiver/_receives e)
                 (> (count (::call/result call)) 1))
        {:line (str (vname id) " = " (vname (:db/id call))
                    "[" (::positionable/position (::call-result/result e) 0) "]")})
      {:input (vname id)
       :position (::positionable/position e)})))

(defmethod line ::call/call
  [db g id]
  (let [e (d/entity db id)
        params (sort-by (comp ::positionable/position ::call-parameter/parameter)
                        (::call/parameter e))
        results (::call/result e)
        callable (::call/callable e)
        [n c] (::namespaced/id callable)
        fqn (if (empty? n) c (str n "." c))
        rid (if (> (count results) 1)
              id (:db/id (first results)))]
    {:line
     (str (vname rid) " = " fqn "("
          (str/join ", "
                    (map (fn [param]
                           (let [pname (-> param ::call-parameter/parameter ::named/name)]
                             (str pname (when pname "=")
                                  (vname (:db/id (::data-receiver/receives param))))))
                         params))
          ")")
     :import (when (seq n) n)}))

(defn generate
  [metadata db]
  (let [{:keys [fn-name] :or {fn-name "f"}} metadata
        g (cfg/db->cfg db)
        order (alg/topsort g)
        lines (map #(line db g %) order)
        imports (into #{} (keep :import) lines)
        inputs (into [] (map :input) (sort-by :position (filter :input lines)))
        outputs (into [] (map :output) (sort-by :position (filter :output lines)))
        lines (into [] (comp (keep :line)
                             (map #(str "  " %)))
                    lines)]
    (str (str/join (map #(str "import " % "\n") imports))
         "\ndef " fn-name "("
         (str/join ", " inputs)
         "):\n"
         (str/join "\n" lines)
         "\n"
         "  return "
         (str/join ", " outputs)
         "\n")))
