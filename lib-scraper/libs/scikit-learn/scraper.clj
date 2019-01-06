(defscraper scikit-learn
  :ecosystem :python
  :python/version "3.7.2"
  :python/dependencies [[scikit-learn "0.20.2"]]

  :seed "https://scikit-learn.org/0.20/modules/classes.html"
  :should-visit #"https://scikit-learn\.org/0\.20/modules/generated/.*"
  :max-pages 1

  :patterns {:name {:attribute :named/name
                    :selector [:children (tag :dt)
                               :children (class :descname)]}
             :description {:attribute :description
                           :selector [:children (tag :dd)
                                      :children
                                      (and (tag :p) (not (class :rubric)))]}
             :parameter-info {:selector [:children (and (tag :span) (class :classifier))]}}

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
          {:trigger :class, :pattern :description}

          ; functions and methods:
          {:trigger :document
           :concept :function
           :selector [:descendants (and (tag :dl) (class :function))]}
          {:trigger :class
           :concept :method
           :selector [:descendants (and (tag :dl) (class :method))]
           :ref-from-trigger :class/method}
          {:trigger :callable, :pattern :name}
          {:trigger :callable, :pattern :description}

          ; parameters:
          {:trigger :callable
           :concept :parameter
           :selector [:descendants
                      (and (tag :th) (find-in-text #"Parameters"))
                      [:ancestors :select (tag :tr) :limit 1]
                      :descendants (tag :dt)]
           :ref-from-trigger :callable/parameter}
          {:trigger :parameter
           :attribute :parameter/name
           :selector [:children (tag :strong)]}
          {:trigger :parameter
           :attribute :parameter/position
           :value :trigger-index}
          {:trigger :parameter
           :attribute :description
           :selector [[:following-siblings :select (tag :dd) :limit 1]
                      :children (tag :p)]}
          {:trigger :parameter
           :attribute :parameter/optional
           :pattern :parameter-info
           :transform #(clojure.string/includes? % "optional")}

          ; datatypes:
          {:trigger :parameter
           :concept :datatype
           :ref-to-trigger :datatype/instance
           :pattern :parameter-info}
          {:trigger :datatype
           :attribute :datatype/name
           :transform #"^[A-Za-z]+"}])
