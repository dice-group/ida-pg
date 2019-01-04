(ns lib-scraper.model.ecosystems.python
  (:require [datascript.core :as d]
            [clojure.spec.alpha :as s]
            [lib-scraper.helpers.transaction :as tx]
            [lib-scraper.model.syntax :refer [defecosystem]]
            [lib-scraper.model.paradigms.oo :refer [oo]]
            [lib-scraper.model.paradigms.functional :refer [functional]]
            [lib-scraper.model.concepts.class :as class]
            [lib-scraper.model.concepts.function :as function]))

(defn postprocess-class
  "Recognize methods named '__init__' as class constructors."
  [db id]
  (let [ctrs (d/q '[:find ?method
                    :in $ ?class
                    :where [?class ::class/method ?method]
                           [?method ::function/name "__init__"]]
                  db id)]
    (map (fn [[ctr]] [:db/add id ::class/constructor ctr])
         ctrs)))

(defecosystem python [oo functional]
  :class {:postprocess (tx/merge (-> oo :class :postprocess)
                                 postprocess-class)}
  :function {:spec ::function})

(s/def ::function (s/or :oo (-> oo :function :spec)
                        :fn (-> functional :function :spec)))

python
