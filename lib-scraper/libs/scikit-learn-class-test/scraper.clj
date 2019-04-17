; Only scrapes sklearn.cluster.KMeans for testing purposes:
(defscraper scikit-learn-cluster
  :extends "../scikit-learn"
  :should-visit #"https://scikit-learn\.org/0\.20/modules/generated/sklearn\.cluster\.KMeans.*"
  :max-pages 1)
