(ns lib-scraper.model.ecosystems.python.class
  (:require [datascript.core :as d]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.model.paradigms.oo.class :as class
                                                  :refer [class]
                                                  :rename {class oo-class}]
            [lib-scraper.model.paradigms.oo.constructor :refer [constructor]]
            [lib-scraper.model.concepts.named :as named])
  (:refer-clojure :exclude [class]))

(defn postprocess
  "Recognize methods named '__init__' as class constructors."
  [db id]
  (let [ctrs (d/q '[:find ?method
                    :in $ ?class
                    :where [?class ::class/method ?method]
                           [?method ::named/name "__init__"]]
                  db id)]
    (mapcat (fn [[ctr]]
              [[:db/add id ::class/constructor ctr]
               [:db/add ctr :type (constructor :ident)]])
            ctrs)))

(defconcept class [oo-class]
  :postprocess postprocess)
