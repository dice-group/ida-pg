(ns librarian.model.common
  "Schema description of universal model attributes that are not specific to any particular concept.")

(def attributes {:type {:db/index true
                        :db/cardinality :db.cardinality/many
                        :db/doc "Type of the concept."}
                 :placeholder {:db/doc "A boolean marking placeholder concepts."}
                 :source {:librarian/internal true
                          :db/doc "The datasource this entity or transaction originates from. Typically a URL."}})
