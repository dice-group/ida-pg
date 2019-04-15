(ns librarian.scraper.attributes)

(def attributes {:tx/source {:librarian/temporary true
                             :librarian/computed true
                             :db/doc "The datasource this transaction originates from."}
                 :tempid {:db/cardinality :db.cardinality/many
                          :db/unique :db.unique/identity
                          :librarian/internal true
                          :librarian/temporary true
                          :librarian/computed true
                          :db/doc "Temporary bookkeeping property used by the scraper."}
                 :allow-incomplete {:db/index true
                                    :librarian/internal true
                                    :librarian/temporary true
                                    :librarian/computed true
                                    :db/doc "Temporary bookkeeping property used by the scraper."}})
