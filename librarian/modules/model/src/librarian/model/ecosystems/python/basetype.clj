(ns librarian.model.ecosystems.python.basetype
  (:require [clojure.spec.alpha :as s]
            [librarian.model.syntax :refer [defconcept]]
            [librarian.model.concepts.basetype :refer [basetype]
             :rename {basetype generic-basetype}]
            [librarian.model.concepts.named :as named]))

(defconcept basetype [generic-basetype]
  :spec ::basetype)

(def basetypes #{"object" "int" "float" "complex" "string" "boolean"})

(s/def ::basetype #(contains? basetypes (::named/name %)))
