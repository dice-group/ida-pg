# Librarian

Librarian is a modular toolkit to programmatically work with software libraries.
This is main entrypoint module for the librarian project.
It offers a unified CLI for all the features of the librarian project and contains a unified REPL environment for development purposes.
Librarian consists of the following submodules:
*   [`librarian/model`](./modules/model): A tool to build abstract models of language ecosystems. It comes with a model for Python but can also be used to describe other ecosystems. It additionally offers a uniform interface to read, write and query facts about a given ecosystem (e.g. the names of the classes in a particular Python namespace). Such collections of model facts are called *scrapes*.
*   [`librarian/scraper`](./modules/scraper): A tool that crawls the documentation of software libraries and produces library scrapes. The scraped information is validated and categorized using a `librarian/model` ecosystem.
*   [`librarian/generator`](./modules/generator): A tools that automatically generates source code from a given library scrape and a user request. 
*   [`librarian/helpers`](./modules/helpers): A collection of various general-purpose helpers that are used by the other librarian modules.

Librarian also comes with a prebuilt scrape for scikit-learn in [`libs/scikit-learn`](./libs/scikit-learn/scraper.clj).

## Installation

Librarian is written in Clojure but it also offers a Java interface.
If you just want to install the modules to your local Maven repo, simply run:
```shell
mvn install
```

## Build

To build the librarian CLI simply run:
```shell
lein uberjar # Creates an executable jar at target/librarian.jar
```

The resulting JAR can be used via the comamnd line.
It is an interface to the [`scraper`](./modules/scraper) and to some parts of the [`model`](./modules/model).
To see all available commands run `java -jar target/librarian.jar -?`.
To get more information about a specific command run `java -jar target/librarian.jar [cmd] -?`.

The CLI commands are documented in more detail in the READMEs of the [`scraper`](./modules/scraper).

## Development

If you want to work on Librarian you will need [Leiningen 2.8](https://leiningen.org/).
Librarian is structured as a [multimodule](https://github.com/jcrossley3/lein-modules) project.
To set up your environment, run:
```shell
lein prepare-repl
# This is a shorthand alias for the following:
# - Installs all modules to your local repo. (lein modules install)
# - Deletes target-dirs from modules. (lein modules clean)
# - Creates Leiningen checkouts. (lein modules :checkouts)
```
Using [checkouts](https://github.com/technomancy/leiningen/blob/stable/doc/TUTORIAL.md#checkout-dependencies) you can test changes across multiple modules without having to reinstall them to your local repo after each change.
The cleaning step is required for hot-code reloading via [clojure.tools.namespace](https://github.com/clojure/tools.namespace) in the REPL.
To get a REPL, just run `lein repl`.
Module specific details are described in the READMEs of the individual modules (linked above).

For simple interoperability with Maven all the Librarian modules also come with an automatically generated pom file.
Those pom files are shipped with Librarian to allow users to integrate Librarian into pure Maven projects without having to install Leiningen or other Clojure specific tooling. 
Please remember to run `lein modules pom` after adding, removing or updating a dependency of a module to update the generated pom files.
