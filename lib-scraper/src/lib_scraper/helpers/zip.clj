(ns lib-scraper.helpers.zip
  (:require [clojure.zip :as zip]
            [clojure.string :as string]))

(defn loc-at-node?
  [node loc]
  (= node (zip/node loc)))

(defn is-parent?
  [parent loc]
  (some (partial loc-at-node? (zip/node parent))
        (take-while some? (iterate zip/up loc))))

(defn loc-content
  [loc]
  (let [node (zip/node loc)]
    (if (string? node)
      (string/trim node)
      (->> node :content
           (filter string?) (reduce str)
           (string/trim)))))

(def step-types {:following (constantly [identity zip/next zip/end?])
                 :children (constantly [zip/down zip/right some?])
                 :siblings (constantly [zip/leftmost zip/right some?])
                 :following-siblings (constantly [identity zip/right some?])
                 :preceding-siblings (constantly [identity zip/left some?])
                 :ancestors (constantly [identity zip/up some?])
                 :descendants (fn [loc]
                                [identity zip/next #(and (not (zip/end? %))
                                                         (is-parent? loc %))])})

(defn select-locs-spread-step
  [type loc]
  (let [[type & {:keys [select while skip limit]}] (if (vector? type) type [type])
        [init next continue?] ((step-types type) loc)]
    (cond->> (take-while continue? (iterate next (init loc)))
      while  (take-while while)
      select (filter select)
      skip   (drop skip)
      limit  (take limit))))

(defn select-locs
  [selectors loc]
  (loop [[selector & selectors] (if (seqable? selectors) selectors [selectors])
         locs [loc]]
    (if-not selector
      locs
      (recur selectors
             (if (fn? selector)
               (keep selector locs)
               (mapcat (partial select-locs-spread-step selector) locs))))))
