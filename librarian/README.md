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

## 1. Getting Started



## 2. Installation

Librarian is written in Clojure but it also offers a [Java interface](./modules/generator/README.md#22-java-applications).
If you just want to install the modules to your local Maven repo, simply run:
```shell
mvn install
```

## 3. Build

To build the librarian CLI simply run:
```shell
lein uberjar # Creates an executable jar at target/librarian.jar
```

The resulting JAR can be used via the comamnd line.
It is an interface to the [scraper](./modules/scraper) and to some parts of the [model](./modules/model).
To see all available commands run `java -jar target/librarian.jar -?`.
To get more information about a specific command run `java -jar target/librarian.jar [cmd] -?`.

The CLI commands are documented in more detail in the README of the [scraper](./modules/scraper).

## 4. Development Setup

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

## 4. Future tasks

-   [x] Develop a universal model to represent software libraries.
-   [x] Develop a tool to automatically build models of libraries from their online documentation.
-   [x] Develop an uninteractive code generator that takes user requirements and a library model and finds source code that satisfies the user's requirements.
-   [ ] **HIGH** Make the code generator **interactive**, i.e. make the user part of the search loop to allow for additional input and clarification during the code generation process. Currently the generator picks whatever it deems to be the most probable choice if there is ambiguity in the user requirements. Multiple parts of the generator need to be extended for this but the core of the current uninteractive A* loop is implemented [here in `search`](modules/generator/src/librarian/generator/generate.clj). Instead of using a tight `iterate` loop to go from one search state to the next, the generator should be able to detect uncertainty (possible via the [cost model](modules/generator/docs/architecture.md#cost-and-heuristic-model)) and allow a third-party (the user) to modify the search state to resolve the uncertainty. Afterwards the search loop can continue until the next point of high uncertainty is reached.
-   [ ] **HIGH** The code executor provided by the `:python` ecosystem model is currently very rudimentary and not capable of handling big loads and of playing nicely in a distributed micro-service architecture, since code is currently just executed in a Python child process of the calling process. Ideally Librarian should be able to automatically generate *"executor" Docker containers* from scrape configurations which would accept code, execute it and respond with the execution result. Those automatically generated containers would allow code execution to be **scaled and distributed**. The container configuration could be done via the currently unused `:meta` attribute of [scrape configurations](modules/scraper/README.md#4-writing-your-own-scrape-configurations) which was added for exactly such use cases.
-   [ ] **HIGH** Improve the **cost model** of the generator. Currently the compatibility between two semantic types is binary. A more fuzzy similarity-based compatibility measure for the semantics of values should be implemented. See [`librarian.generator.cost`](modules/generator/src/librarian/generator/cost.clj).
-   [ ] **HIGH** Improve the **heuristic function** of the generator. The current heuristic is fairly conservative in order to stay admissible. Without a relatively high weight to make the WA* search more depth-first like, this conservative heuristic would often consider fruitless paths. More research is necessary in order to get more accurate heuristic.
-   [ ] **MEDIUM** Consider ways to further improve the **performance of the generator**. The generator already caches multiple types of query results and tries to reuse previous computations if possible. However there are still many opportunities to further reduce redundant computations. It needs to be analyzed which remaining optimizations are feasible. A major candidate to improve performance would be to make the A* search loop multithreaded or possible even parallelize individual search steps (it is however not yet clear whether the resulting overhead would outweigh the benefits).
-   [ ] **LOW** The **CFG visualizations** that are part of the dev tooling for the generator currently only support the Atom editor via [proto-repl](https://github.com/jasongilman/proto-repl). Support for other visualization backends should be added so that other editors and IDEs are usable equally well.
