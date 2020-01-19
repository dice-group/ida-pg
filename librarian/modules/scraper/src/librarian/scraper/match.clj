(ns librarian.scraper.match
  (:require [clojure.string :as string])
  (:import (edu.uci.ics.crawler4j.crawler Page)
           (edu.uci.ics.crawler4j.url WebURL)))

(defn match-url
  "Returns a function that checks whether a page reached by crawler4j matches the given string or regex pattern."
  [pattern]
  (if (string? pattern)
    (fn [_, ^Page page, ^WebURL url]
      (= pattern (.getURL url)))
    (fn [_, ^Page page, ^WebURL url]
      (some? (re-matches pattern (.getURL url))))))

(defn require-classes
  "Returns a function that checks whether the anchor element pointing to a candidate page has all of the required classes."
  [& required-classes]
  (fn [_, ^Page page, ^WebURL url]
    (let [{:strs [class] :or {class ""}} (into {} (.getAttributes url))
          classes (set (map string/lower-case (string/split class #"\s+")))]
      (every? (comp classes string/lower-case) required-classes))))
