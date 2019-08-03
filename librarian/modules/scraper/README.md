# librarian/scraper

Crawls through the documentation of software libraries and transforms them into a standardized format.
librarian-scraper is a tool that uses a scrape configuration to crawl the documentation of a software library.
The scraped information will be automatically linked, sanitized, validated and then written to a scrape database file.

<img src="docs/lib-scraper-overview.svg">

**Architecture overview:** An introduction to the working principle of the scraper can be found [here](./docs/architecture.md).

## 1. Scraping a library

The scraper can be used via the unified [Librarian-CLI](../..).
To create a scrape you can run:
```shell
java -jar target/librarian.jar scrape libs/scikit-learn
```
The `scrape` command requires the path to a scrape configuration.
Librarian comes with a scrape configuration for scikit-learn at [`libs/scikit-learn/scraper.clj`](../../libs/scikit-learn/scraper.clj).
If the configuration is called `scraper.clj`, the filename can be omitted as seen above.

The resulting scrape will be written to `scrape.db` in the parent directory of the scrape configuration by default.
This can be changed by providing an output path as a second argument to `scrape`.

For debugging purposes the scraper also provides the commands `print-scrape` and `print-config` to print validated and postprocessed versions of the scrape database and configuration.

## 2. Querying a scraped library via the CLI

Despite the fact that scrapes are meant to be used programmatically via the librarian-scraper Java API, they can also be queried via the CLI for debugging and testing purposes.

librarian-scraper stores scrapes as gzipped serializations of [Datascript](https://github.com/tonsky/datascript) databases.
Those scrape databases can be queries using [Datalog](https://docs.datomic.com/on-prem/query.html) queries:
```shell
java -jar target/librarian.jar query libs/scikit-learn \
  '[:find ?name :where [?p :type :namespace] [?p :namespace/name ?name]]'
# => Prints the names of all the namespaces of scikit-learn.
```
A good introduction to Datalog can be found [here](http://www.learndatalogtoday.org/).

To see which attributes and types are available for queries, you can use the `print-schema` command:
```shell
java -jar target/librarian.jar print-schema python
# => Prints the attributes and concept types of python library scrapes.
```

Alternatively the CLI also supports so called [pull queries](https://docs.datomic.com/on-prem/pull.html) that make it easy to perform recursive traversals through the attribute graph:
```shell
java -jar target/librarian.jar pull libs/scikit-learn \
	'[*]' '[:class/id "sklearn.cluster.KMeans"]'
# => Prints all attributes and subattributes of the KMeans class.
```

## 3. Writing your own scrape configurations

A scraper is configured via a `scraper.clj` file which has the following [EDN](https://github.com/edn-format/edn)-like structure:
```clojure
(defscraper my-library
  :ecosystem :python ; Currently only python support is provided.

  :seed "URL" ; Initial page for the library crawling.
  :should-visit #"URL regex" ; A regular expression matching the URLs that should be crawled.
  
  ; Patterns: A map from pattern names to patterns, i.e. hook templates.
  :patterns
  {:my-pattern { ... definition of hook properties ... }}
  
  ; Hooks: A collection of hooks to be used to extract content from crawled pages:
  :hooks
  [; 1. Concept hooks for creating concepts:
   {:triggered-by :some-trigger ; All hooks need a trigger.
	:concept :some-concept ; Alias of the concept that should be created.
	:selector [ ... selector definition ... ] ; Selector to find all instances of the concept relative to the trigger.
	:ref-to-trigger :some-concept/my-trigger ; Used to add a reference attribute from concepts created by this hook to their triggering concept.
	:ref-from-trigger :some-trigger/my-concept} ; Used to add a reference attribute from the triggering concept to the created concepts.
   
   ; 2. Attribute hooks to attach information to concepts: 
   {:triggered-by :some-trigger
	:attribute :some-trigger/some-attribute ; Alias of the attribute that should be added to the triggering concept.
	:selector [ ... selector definition ... ] ; Selector to find the values for the created attributes relative to the triggering concept.
	:value :content | :trigger-index | "some string" | 42 ; Controls which value is used for the created attribute.
	:transform #"value regex" | some-function ; A regex or function to transform the selected attribute value or to select parts of it.
	}
   
   ; 3. General properties of hooks:
   {:triggered-by :document ; There needs to be at least one initial hook, that is triggered by the :document root.
 	:pattern :my-pattern}] ; Hooks can inherit parts of their definition from the defined patterns.
  
  ; Snippets: Predefined code patterns for the scraped library to speed up code generation.
  :snippets
  [[; A snippet is a partial control flow graph (CFG) consisting of control flow nodes with references to arbitrary concepts:
    {:type :call ; A call node describes the call to some callable (function, method, etc.).
     ; Next we describe that the call should go to some arbitrary callable in a specified namespace.
	 ; Setting :placeholder to true marks the CFG node as a proxy for a multitude of specific concepts that it should be replaced with:
	 :callable {:type :function
                :placeholder true
				; The underscore in the attribute name means, that a reverse reference is required between the placeholder function and namespace,
				; i.e. the specified namespace should have a membership reference to the concept:
                :namespace/_member {:type :namespace
                                    :name "some namespace name"}}
	 ; We also require that the call accepts a parameter with a certain name:
     :parameter {:type :call-parameter
		         :parameter {:type :parameter
					         :placeholder true
							 :name "some parameter name"}}
	 ; Finally we declare that whatever the specified family of calls returns, has the role-type :my-role
	 ; This is useful to manually state which functions return values that can be interpreted and used in a certain way.
	 :result {:type :call-result
		      :result {:type :result
				       :placeholder true}
		      :datatype {:type :role-type
				         :id :my-role}}}
    { ... another CFG node ... }]
   [... another snippet ...]])
```

A complete example configuration with this structure can be found at [`libs/scikit-learn/scraper.clj`](../../libs/scikit-learn/scraper.clj).

### 3.1. Hooks

Hooks control how content is extracted from a page.
For a formal perspective on how they work, see the [architecture description](./docs/architecture.md).
The basic idea is to overlay a concept graph represented by a Datascript database on top of the crawled DOM trees of a library.
This means that every concept and attribute value in the database is derived from some specific location in a DOM tree.

Each hook has a *trigger*, which is typically the alias of a concept.
Whenever a new concept instance is created, all the hooks with that concept as their trigger are activated.
When activated hooks then create new concepts or attribute which in turns triggers other hooks.
This recursive chain of hook activations is set off by an initial trigger `:document` at the root of the DOM tree of each crawled web page.

This is a simple example of how the concept graph overlaying a DOM tree might look like:

<img src="docs/lib-scraper-example.svg">

The hooks that produce this overlayed concept graph might look like this:
```clojure
[; 1:
 {:triggered-by :document
  :concept :function
  :selector [:descendants (and (tag :div) (class :func))]}
 ; 2:
 {:triggered-by :function
  :attribute :function/name
  :selector [:children (and (tag :span) (class :fname))
  :value :content ; Use the text content of the selected node as the value.
  :transform #"^[a-zA-Z0-9_]+"]} ; Don't include the parentheses.
 ; 3:
 {:triggered-by :function
  :concept :parameter
  :selector [:children (and (tag :div) (class :params))
             :children (tag :b)]
  :ref-from-trigger :function/parameter}
 ; 4:
 {:triggered-by :parameter
  :attribute :parameter/name
  ; The concept is already associated to the node containing the name, thus no selector is required.
  ; The transformer is also not required here.
  :value :content}
 ; 5:
 {:triggered-by :parameter
  :attribute :parameter/description
  ; Go through all the siblings following the <b> tag and select the first <span> with class "desc":
  :selector [[:following-siblings :select (and (tag :span) (class :desc)) :limit 1]]
  :value :content}
 ]
```

#### 3.1.1. Concept Hooks

Concept hooks are used to create concepts.
