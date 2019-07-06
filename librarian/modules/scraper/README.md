# librarian/scraper

Crawls through the documentation of software libraries and transforms them into a standardized format.
librarian-scraper is a tool that uses a scrape configuration to crawl the documentation of a software library.
The scraped information will be automatically linked, sanitized, validated and then written to a scrape database file.

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

**[TODO: Documentation]**
