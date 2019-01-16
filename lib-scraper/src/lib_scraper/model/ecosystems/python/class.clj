(ns lib-scraper.model.ecosystems.python.class
  (:require [datascript.core :as d]
            [lib-scraper.model.syntax :refer [defconcept]]
            [lib-scraper.model.paradigms.oo.class :as class
                                                  :refer [class]
                                                  :rename {class oo-class}]
            [lib-scraper.model.ecosystems.python.constructor :as constructor
                                                             :refer [constructor]]
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
              [[:db/add ctr ::constructor/class id]
               [:db/add ctr :type (constructor :ident)]
               [:db/add id ::class/constructor ctr]])
            ctrs)))

(defconcept class [oo-class]
  :preprocess {::class/constructor
               (fn [ctr id] [[:db/add ctr ::constructor/class id]])}
  :postprocess postprocess)
