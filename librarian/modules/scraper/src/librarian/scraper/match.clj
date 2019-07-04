(ns librarian.scraper.match
  (:require [clojure.string :as string])
  (:import (edu.uci.ics.crawler4j.crawler Page)
           (edu.uci.ics.crawler4j.url WebURL)))

(defn match-url
  [pattern]
  (if (string? pattern)
    (fn [_, ^Page page, ^WebURL url]
      (= pattern (.getURL url)))
    (fn [_, ^Page page, ^WebURL url]
      (some? (re-matches pattern (.getURL url))))))

(defn require-classes
  [& required-classes]
  (fn [_, ^Page page, ^WebURL url]
    (let [{:strs [class] :or {class ""}} (into {} (.getAttributes url))
          classes (set (map string/lower-case (string/split class #"\s+")))]
      (every? (comp classes string/lower-case) required-classes))))
