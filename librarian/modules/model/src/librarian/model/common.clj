(ns librarian.model.common)

(def attributes {:type {:db/index true
                        :db/cardinality :db.cardinality/many
                        :db/doc "Type of the concept."}
                 :placeholder {:db/doc "A boolean marking placeholder concepts."}
                 :description {:db/cardinality :db.cardinality/many
                               :db/doc "Description of the concept."}
                 :description-summary {:db/doc "Short summarized description of the concept."}
                 :source {:librarian/internal true
                          :db/doc "The datasource this entity or transaction originates from. Typically a URL."}})
