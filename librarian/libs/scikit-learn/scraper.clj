(defscraper scikit-learn
  :ecosystem :python
  :meta {:python/version "3.7.2"
         :python/dependencies [[scikit-learn "0.20.2"]]}

  :seed "https://scikit-learn.org/0.20/modules/classes.html"
  :should-visit #"https://scikit-learn\.org/0\.20/modules/generated/.*"

  :patterns {:name {:attribute :named/name
                    :selector [:children (tag :dt)
                               :children (class :descname)]}
             :description {:concept :semantic-type
                           :selector [:children (tag :dd)
                                      :children (and (tag :p) (not (class :rubric)))]
                           :ref-from-trigger :typed/datatype
                           :triggers :description}
             :io-container-info {:selector [:children (and (tag :span) (class :classifier))]}}

  :hooks [; namespaces:
          {:triggered-by :namespaced
           :concept :namespace
           :selector [:children (tag :dt)
                      :children (class :descclassname)]
           :ref-to-trigger :namespace/member}
          {:triggered-by :namespace
           :attribute :namespace/name
           :transform #".*[^.]"}

          ; classes:
          {:triggered-by :document
           :concept :class
           :selector [:descendants (and (tag :dl) (class :class))]}
          {:triggered-by :class, :pattern :name}
          {:triggered-by :class, :pattern :description}
          {:triggered-by :class
           :concept :constructor
           :selector [[:descendants :select (tag :table) :limit 1]]
           :ref-from-trigger :class/constructor}

          ; functions and methods:
          {:triggered-by :document
           :concept :function
           :selector [:descendants (and (tag :dl) (class :function))]}
          {:triggered-by :class
           :concept :method
           :selector [:descendants (and (tag :dl) (class :method))]
           :ref-from-trigger :class/method}
          {:triggered-by :callable, :pattern :name}
          {:triggered-by :callable, :pattern :description}

          ; parameters:
          {:triggered-by :callable
           :concept :parameter
           :selector [[:descendants
                       :select (and (tag :th) (find-in-text #"Parameters"))
                       :limit 1]
                      [:ancestors :select (tag :tr) :limit 1]
                      :descendants (tag :dt)]
           :ref-from-trigger :callable/parameter}
          {:triggered-by :parameter
           :attribute :parameter/optional
           :pattern :io-container-info
           :transform #(or (clojure.string/includes? % "optional")
                           (clojure.string/includes? % "default"))}

          ; results:
          {:triggered-by :callable
           :concept :result
           :selector [[:descendants
                       :select (and (tag :th) (find-in-text #"Returns"))
                       :limit 1]
                      [:ancestors :select (tag :tr) :limit 1]
                      :descendants (tag :dt)]
           :ref-from-trigger :callable/result}

          ; io-containers (parameters & results):
          {:triggered-by :io-container
           :attribute :io-container/name
           :selector [:children (tag :strong)]}
          {:triggered-by :io-container
           :concept :semantic-type
           :selector [:children (tag :strong)]
           :ref-from-trigger :io-container/datatype
           :triggers :name-type}
          {:triggered-by :io-container
           :attribute :io-container/position
           :value :trigger-index}
          {:triggered-by :io-container
           :concept :semantic-type
           :selector [[:following-siblings :select (tag :dd) :limit 1]
                      :children (tag :p)]
           :ref-from-trigger :io-container/datatype
           :triggers :description}

          ; datatypes:
          {:triggered-by :io-container
           :concept :basetype
           :ref-from-trigger :io-container/datatype
           :pattern :io-container-info}
          {:triggered-by :basetype
           :attribute :basetype/name
           :transform #"^[A-Za-z]+(?!.*ndarray)"}
          {:triggered-by :io-container
           :concept :constant
           :ref-from-trigger :io-container/datatype
           :pattern :io-container-info}
          {:triggered-by :constant
           :attribute :constant/value
           :transform #"(?<=[‘“'\"]).*?(?=[’”'\"])"}

          ; name-types:
          {:triggered-by :name-type
           :attribute :semantic-type/key
           :value "name"}
          {:triggered-by :name-type
           :attribute :semantic-type/value
           :value :content}

          ; descriptions:
          {:triggered-by :description
           :attribute :semantic-type/key
           :value "description"}
          {:triggered-by :description
           :attribute :semantic-type/value
           :value :content}
          {:triggered-by :description
           :attribute :semantic-type/position
           :value :trigger-index}]

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
