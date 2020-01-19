# librarian/helpers

A collection of various general-purpose helpers that are used by the other librarian modules.

Contains the following helpers:
-   `map`: Helpers to map over hashmaps and other general purpose transducers.
-   `predicate`: Helpers to compose boolean predicate functions.
-   `spec`: Clojure spec helpers, e.g. to validate Datascript entities. 
	While they look like normal hashmaps, they cannot be validated using the standard `keys` spec, since their values are materialized lazily.
-   `transaction`: Helpers to automatically build and analyze Datascript transaction vectors.
-   `transients`: Helpers to work with transient collections.
-   `zip`: Contains the implementation of the [selector vector DSL](../scraper/README.md#412-the-dom-selector-dsl) that is used by the scraper.
