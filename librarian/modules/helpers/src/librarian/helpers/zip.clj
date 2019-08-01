(ns librarian.helpers.zip
  "Helpers to work with Clojure zippers."
  (:require [clojure.zip :as zip]
            [clojure.string :as string]))

(defn loc-at-node?
  "Returns whether a given location points at a given node."
  [node loc]
  (= node (zip/node loc)))

(defn is-parent?
  "Returns whether a given `parent` location is an ancestor of `loc`.
   Also returns true if `(= parent loc)`."
  [parent loc]
  (some (partial loc-at-node? (zip/node parent))
        (take-while some? (iterate zip/up loc))))

(defn loc-content
  "Returns the concatenated string content found in the HTML node pointed at by `loc`."
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
    (eduction (comp (take-while continue?)
                    (if while (take-while while) identity)
                    (if select (filter select) identity)
                    (if skip (drop skip) identity)
                    (if limit (take limit) identity))
              (iterate next (init loc)))))

(defn select-locs
  "Takes a selector sequence and a starting location and returns a sequence of selected locations."
  [selectors loc]
  (loop [[selector & selectors] (if (seqable? selectors) selectors [selectors])
         locs [loc]]
    (if-not selector
      locs
      (recur selectors
             (if (fn? selector)
               (keep selector locs)
               (mapcat #(select-locs-spread-step selector %) locs))))))
