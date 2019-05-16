(defscraper scikit-learn
  :ecosystem :python
  :meta {:python/version "3.7.2"
         :python/dependencies [[scikit-learn "0.20.2"]]}

  :seed "https://scikit-learn.org/0.20/modules/classes.html"
  :should-visit #"https://scikit-learn\.org/0\.20/modules/generated/.*"

  :patterns {:name {:attribute :named/name
                    :selector [:children (tag :dt)
                               :children (class :descname)]}
             :description-summary {:attribute :description-summary
                                   :selector [:children (tag :dd)
                                              [:children
                                               :select (and (tag :p) (not (class :rubric)))
                                               :limit 1]]}
             :description {:attribute :description
                           :selector [:children (tag :dd)
                                      [:children
                                       :select (and (tag :p) (not (class :rubric)))]]}
             :io-container-info {:selector [:children (and (tag :span) (class :classifier))]}}

  :hooks [; namespaces:
          {:trigger :namespaced
           :concept :namespace
           :selector [:children (tag :dt)
                      :children (class :descclassname)]
           :ref-to-trigger :namespace/member}
          {:trigger :namespace
           :attribute :namespace/name
           :transform #".*[^.]"}

          ; classes:
          {:trigger :document
           :concept :class
           :selector [:descendants (and (tag :dl) (class :class))]}
          {:trigger :class, :pattern :name}
          {:trigger :class, :pattern :description-summary}
          {:trigger :class, :pattern :description}
          {:trigger :class
           :concept :constructor
           :selector [[:descendants :select (tag :table) :limit 1]]
           :ref-from-trigger :class/constructor}

          ; functions and methods:
          {:trigger :document
           :concept :function
           :selector [:descendants (and (tag :dl) (class :function))]}
          {:trigger :class
           :concept :method
           :selector [:descendants (and (tag :dl) (class :method))]
           :ref-from-trigger :class/method}
          {:trigger :callable, :pattern :name}
          {:trigger :callable, :pattern :description-summary}
          {:trigger :callable, :pattern :description}

          ; parameters:
          {:trigger :callable
           :concept :parameter
           :selector [[:descendants
                       :select (and (tag :th) (find-in-text #"Parameters"))
                       :limit 1]
                      [:ancestors :select (tag :tr) :limit 1]
                      :descendants (tag :dt)]
           :ref-from-trigger :callable/parameter}
          {:trigger :parameter
           :attribute :parameter/optional
           :pattern :io-container-info
           :transform #(or (clojure.string/includes? % "optional")
                           (clojure.string/includes? % "default"))}

          ; results:
          {:trigger :callable
           :concept :result
           :selector [[:descendants
                       :select (and (tag :th) (find-in-text #"Returns"))
                       :limit 1]
                      [:ancestors :select (tag :tr) :limit 1]
                      :descendants (tag :dt)]
           :ref-from-trigger :callable/result}

          ; io-containers (parameters & results):
          {:trigger :io-container
           :attribute :io-container/name
           :selector [:children (tag :strong)]}
          {:trigger :io-container
           :attribute :io-container/position
           :value :trigger-index}
          {:trigger :io-container
           :attribute [:description-summary :description]
           :selector [[:following-siblings :select (tag :dd) :limit 1]
                      :children (tag :p)]}

          ; datatypes:
          {:trigger :io-container
           :concept :basetype
           :ref-from-trigger :io-container/datatype
           :pattern :io-container-info}
          {:trigger :basetype
           :attribute :basetype/name
           :transform #"^[A-Za-z]+"}]

  :snippets [[{:type :call
               :callable {:type :function
                          :placeholder true
                          :namespace/_member {:type :namespace
                                              :name "sklearn.cluster"}}
               :parameter {:type :call-parameter
                           :datatype {:type :role-type
                                      :id :dataset}
                           :parameter {:type :parameter
                                       :placeholder true
                                       :name "X"}}
               :result {:type :call-result
                        :datatype {:type :role-type
                                   :id :labels}
                        :result {:type :result
                                 :placeholder true
                                 :name "label"}}}]])
