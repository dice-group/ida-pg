# Librarian

Librarian is a modular toolkit to programmatically work with software libraries.
It consists of the following modules:
* `librarian/model`: A tool to build abstract models of language ecosystems. It comes with a model for Python but can also be used to describe other ecosystems. It additionally offers a uniform interface to read, write and query facts about a given ecosystem (e.g. the names of the classes in a particular Python namespace). Such collections of model facts are called *scrapes*.
* `librarian/scraper`: A tool that crawls the documentation of software libraries and produces library scrapes. The scraped information is validated and categorized using a `librarian/model` ecosystem.
* `librarian/generator` **(NOT IMPLEMENTED YET)**: A tools that automatically generates source code from a given library scrape and a user request. 

Librarian also comes with a prebuilt scrape for scikit-learn in `libs/scikit-learn`.

## Installation

Librarian is written in Clojure but it also offers a Java interface.
If you just want to install the modules to your local Maven repo, simply run:
```shell
mvn install
```

## Development

If you want to work on Librarian you will need [Leiningen 2.8](https://leiningen.org/).
Librarian is structured as a [multimodule](https://github.com/jcrossley3/lein-modules) project.
To set up your environment, run:
```shell
lein modules install # Installs all modules to your local repo.
lein modules :checkouts # Creates Leiningen checkouts for all modules.
```
Using [checkouts](https://github.com/technomancy/leiningen/blob/stable/doc/TUTORIAL.md#checkout-dependencies) you can test changes across multiple modules without having to reinstall them to your local repo after each change.
To get a REPL, just run `lein repl` in the directory of the module you want to work on.
Module specific details are described in the READMEs of the individual modules.

For simple interoperability with Maven all the Librarian modules also come with an automatically generated pom file.
Those pom files are shipped with Librarian to allow users to integrate Librarian into pure Maven projects without having to install Leiningen or other Clojure specific tooling. 
Please remember to run `lein modules pom` after adding, removing or updating a dependency of a module to update the generated pom files.
