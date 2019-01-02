(ns lib-scraper.model.ecosystems.python
  (:require [datascript.core :as d]
            [lib-scraper.helpers.transaction :refer [merge-fn-maps]]
            [lib-scraper.model.paradigms.oo :as oo]
            [lib-scraper.model.paradigms.functional :as functional]
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

(def ecosystem {:concept (merge oo/concept
                                functional/concept)
                :postprocess (merge-fn-maps oo/postprocess
                                            functional/postprocess
                                            {::class/concept postprocess-class})})
