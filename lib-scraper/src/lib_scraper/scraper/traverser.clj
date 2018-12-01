(ns lib-scraper.scraper.traverser
  (:require [lib-scraper.model.core :as m]))

(defn- queue
  ([] (clojure.lang.PersistentQueue/EMPTY))
  ([coll]
   (reduce conj clojure.lang.PersistentQueue/EMPTY coll)))

(defn- traverser-fn
  [db doc]
  (let [queue (queue [:document])]
    queue))

(defn traverser
  [hooks]
  (let [db (m/create-db)]
    {:db db
     :traverser (partial traverser-fn db)}))
