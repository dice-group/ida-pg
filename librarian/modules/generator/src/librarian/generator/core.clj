(ns librarian.generator.core
  (:require [datascript.core :as d]
            [clucie.core :as clucie]
            [clucie.store :as cstore]
            [clucie.document :as cdoc]
            [clucie.analysis :as canalysis]
            [librarian.model.io.scrape :as scrape]
            [librarian.model.syntax :refer [instanciate instances->tx]]
            [librarian.model.concepts.call-parameter :as call-parameter]
            [librarian.model.concepts.call-value :as call-value]
            [librarian.model.concepts.call-result :as call-result]
            [librarian.model.concepts.goal-type :as goal-type]
            [librarian.model.concepts.semantic-type :as semantic-type]
            [librarian.model.concepts.named :as named]
            [librarian.model.concepts.typed :as typed]
            [librarian.generator.query :as gq]
            [repl-tools :as rt])
  (:import (org.apache.lucene.analysis.en EnglishAnalyzer)))

(defn scrape->docs
  [{:keys [db ecosystem]}]
  (->> db
       (d/q ecosystem
            '[:find ?c (distinct ?t) (distinct ?d) ?n
              :where [?c :type ?t]
                     [?c :description ?d]
                     [(get-else $ ?c ::named/name :unnamed) ?n]])
       (map (fn [[id type description name]]
              {:id id
               :name name
               ::clucie/raw-fields
               (concat (->> type
                            (mapcat ancestors)
                            (distinct)
                            (map #(cdoc/field :type (str %)
                                              {:indexed? true})))
                       (->> description
                            (map #(cdoc/field :description %
                                              {:indexed? true
                                               :tokenized? true}))))}))))

(defn add-scrape!
  [index scrape]
  (clucie/add! index (scrape->docs scrape) [:id]))

(defn scrape->index
  [scrape]
  (let [index (cstore/memory-store)]
    (add-scrape! index scrape)
    index))

(def analyzer (canalysis/analyzer-mapping (EnglishAnalyzer.)
                                          {:type (canalysis/keyword-analyzer)}))

(defn search
  [index max type description]
  (->> (clucie/search index
                      [{:type (str type)}
                       {:description description}]
                      max analyzer)
       (map (juxt #(Integer. (:id %))
                  (comp :score meta)))
       (into {})))

(defn initial-state
  [scrape goals]
  (let [concepts (concat (mapv (fn [goal]
                                 (instanciate call-parameter/call-parameter
                                   :datatype (instanciate goal-type/goal-type
                                               :id goal)))
                               goals)
                         [(instanciate call-value/call-value
                            :value "Hello World!"
                            :datatype (instanciate goal-type/goal-type
                                        :id :labels))])]
    (println (instances->tx concepts))
    {:predecessor nil
     :cost 0
     :db (-> (:db scrape)
             (d/with (instances->tx concepts))
             :db-after)}))

(defn next-flaw
  [state]
  (d/q '[:find ?flaw .
         :in $ %
         :where (type ?flaw ::call-parameter/call-parameter)
                (not [?flaw ::call-parameter/receives ?value])]
       (:db state) gq/rules))

(defn receive-actions
  [state flaw]
  (let [solutions
        (d/q '[:find [?solution ...]
               :in $ % ?flaw
               :where (or (type ?solution ::call-result/call-result)
                          (type ?solution ::call-value/call-value))
                      (not-join [?flaw ?solution]
                        [?flaw ::typed/datatype ?flaw-type]
                        [?solution ::typed/datatype ?solution-type]
                        (not (type ?flaw-type ::semantic-type/semantic-type))
                        (not (type ?solution-type ::semantic-type/semantic-type))
                        (not (subdatatype ?flaw-type ?solution-type)))]
             (:db state) gq/rules
             flaw)]
    (map (fn [solution]
           {:cost 1
            :tx [[:db/add flaw ::call-parameter/receives solution]]})
         solutions)))

(defn apply-action
  [state action]
  {:predecessor state
   :cost (+ (:cost state) (:cost action))
   :db (-> (:db state) (d/with (:tx action)) :db-after)})

(defn successors
  [state]
  (let [flaw (next-flaw state)
        actions (receive-actions state flaw)]
    (map (partial apply-action state)
         actions)))

(try
  (let [scrape (scrape/read-scrape "libs/scikit-learn-class-test")
        state (initial-state scrape [:labels])
        succ (successors state)]
    (rt/show-state (first succ)))
  (catch Exception e
    (.println *err* e)))
