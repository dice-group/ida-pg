# lib-scraper

Crawls through the documentation of software libraries and transforms them into a standardized format.

The project consists of two parts:
1. **A scraper** that uses a scrape configuration to crawl the documentation of a software library. The scraped information will be automatically linked, sanitized, validated and then written to a scrape database file.
2. **A code generator** that can use a scrape database to generate function or method calls.

## 1. The scraper

### 1.1. Build

First install the following tools:
* JDK >=8
* [Leiningen 2.8](https://leiningen.org/)

Then run `lein build-scraper` to create an executable uberjar in `target/lib-scraper.jar`.

### 1.2. Scraping a library

The scraper provides a simple CLI to create scrapes:
```shell
java -jar target/lib-scraper.jar scrape libs/scikit-learn
```
The `scrape` command requires the path to a scrape configuration.
lib-scraper comes with a scrape configuration for scikit-learn at `libs/scikit-learn/scraper.clj`.
If the configuration is called `scraper.clj`, the filename can be omitted as seen above.

The resulting scrape will be written to `scrape.db` in the parent directory of the scrape configuration by default.
This can be changed by providing an output path as a second argument to `scrape`.

For debugging purposes the scraper also provides the commands `print-scrape` and `print-config` to print validated and postprocessed versions of the scrape database and configuration.

To see all available commands run `java -jar target/lib-scraper.jar -?`.
To get more information about a specific command run `java -jar target/lib-scraper.jar [cmd] -?`.

### 1.3. Querying a scraped library via the CLI

Despite the fact that scrapes are meant to be used programmatically via the lib-scraper Java API, they can also be queried via the CLI for debugging and testing purposes.

lib-scraper stores scrapes as gzipped serializations of [Datascript](https://github.com/tonsky/datascript) databases.
Those scrape databases can be queries using [Datalog](https://docs.datomic.com/on-prem/query.html) queries:
```shell
java -jar target/lib-scraper.jar query libs/scikit-learn \
  '[:find ?name :where [?p :type :namespace] [?p :namespace/name ?name]]'
# => Prints the names of all the namespaces of scikit-learn.
```

A good introduction to Datalog can be found [here](http://www.learndatalogtoday.org/).
To see which attributes and types are available for queries, you can use the `print-schema` command:
```shell
java -jar target/lib-scraper.jar print-schema python
```

### 1.4. Writing your own scrape configurations

**[TODO: Documentation]**

## 2. The code generator

**[TODO: Implementation + Documentation]**
