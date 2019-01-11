(defscraper scikit-learn-cluster
  :extends "../scikit-learn/scraper.clj"
  :should-visit #"https://scikit-learn\.org/0\.20/modules/generated/sklearn\.cluster.*")
