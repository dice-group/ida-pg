(ns lib-scraper.helpers.zip
  (:require [clojure.zip :as zip]))

(defn loc-at-node?
  [node loc]
  (= node (zip/node loc)))

(defn is-parent?
  [parent loc]
  (some (partial loc-at-node? (zip/node parent))
        (take-while some? (iterate zip/up loc))))
