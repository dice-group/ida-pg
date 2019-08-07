# librarian/model

The Librarian model contains the definitions of the core concepts that are needed to describe software libraries and *control flow graphs* (CFGs) that call parts of those libraries.
The model consists of three layers:
1.  **Concepts** are the fundamental building block of the model.
	A concept describes any entity that can be found in a library or a CFG, e.g. a function, a datatype or the call to a function.
2.  **Paradigms** are a combination of a collection of concepts that are relevant for the paradigm and an optional collection of concept instances that can always be found in the paradigm.
	Examples for paradigms are object-orientation, functional programming or logic programming.
3.  **Ecosystems** are a special type of paradigm.
	They represent a particular combination of paradigms, syntax and tooling that make up a language ecosystem.
	Python for example has concepts from the object-oriented and functional paradigm, represents those concepts via the syntax of the Python language and allows executing CFGs expressed in that syntax via the Python interpreter.
	All the information required to represent and execute a program in a given ecosystem is encoded in the definition of an ecosystem model.
	
Librarian comes with a collection of general purpose concepts that can be found across many paradigms and ecosystems.
It also comes with basic paradigm definitions for object-oriented and functional programming.
Lastly it also provides an ecosystem for Python.

In the following sections we will first describe how to define such components, then the mentioned builtin components are described in detail.

## 1. Definition of Model Components

The model offers three macros to define concepts, paradigms and ecosystem:
`defconcept`, `defparadigm` and `defecosystem`.
They can be found in the [`librarian.model.syntax`](src/librarian/model/syntax.clj) namespace.

### 1.1. Defining Concepts

```clojure
(defconcept name [optional vector of parent concepts ...]
  :attributes { ... A datascript schema for the concept ... }
  :preprocess { ... A map from attributes to preprocessor functions ... }
  :postprocess A postprocessor function
  :spec A Clojure spec to validate concept instances)
```

Defines a new concept with name `name` in the current namespace.
The concept is described by a sequence of key-value pairs.
A concept description can contain the following pairs, all of which are optional:
-   `:attributes`: A map describing datascript attributes, i.e. a [database schema](https://github.com/kristianmandrup/datascript-tutorial/blob/master/create_schema.md) for the concept.
-   `:preprocess`: A map from attributes to preprocessor functions for those attributes. Useful to mirror attribute values.
-   `:postprocess`: A function that takes a datascript database and the id of an instance of this concept. The function returns a datascript transaction that should be executed as part of the transaction that adds the given concept. Useful to compute derived attributes for concept instances.
-   `:spec`: A [Clojure spec](https://clojure.org/guides/spec) that should be used to check the validity of supposed instances of this concept.

In addition to the key-value pairs the concept description can be preceded by a vector of concepts that the newly defined concept should inherit from.

Example:
```clojure
(require '[librarian.model.syntax :refer [defconcept]]
         '[clojure.spec.alpha :as s]
		 '[librarian.helpers.spec :as hs]
		 '[my.other.concepts :refer [parent-concept1 other-concept]])

(defconcept parent-concept2) ; Useless, but allowed.

(defconcept my-concept [parent-concept1 parent-concept2]
  :attributes {::x {:db/doc "A test attribute."}
               ::y {:db/valueType :db.type/ref
			        :db/doc "A reference to another concept."}}
  :postprocess (fn [db id] [:db/add id ::x 42]) ; All instances get x=42 auto-assigned.
  :spec ::my-concept)

(s/def ::my-concept (hs/entity-keys :req [::x ::y]))
(s/def ::x int?)
(s/def ::y (hs/instance? other-concept))
```

It is strongly recommended that all concept attributes are fully-qualified keywords (`::attribute` instead of `:attribute`) to prevent accidental collisions with other concepts.
It is also recommended to create a separate namespace for each concept (not like the example where `parent-concept2` and `my-concept` are defined in a single namespace).

### 1.2. Defining Paradigms

```clojure
(defparadigm name [optional vector of parent paradigms ...]
  :concepts { ... A map of concept aliases to concepts ... }
  :builtins [ ... A collection of concept instance descriptions ... ])
```

Defines a new paradigm with name `name` in the current namespace.
The name is followed by an optional vector of paradigms that should be included in the new paradigm.
Then a sequence of key-value pairs follows:
-   `:concepts`: A map of unnamespaced concept alias keywords to concepts.
-   `:builtins`: A collection of builtin concept instances in the defined paradigm.
	The predefined instances should be created via [`librarian.model.syntax/instanciate`](src/librarian/model/syntax.clj).
	Builtins are intended to define things like the global `Object` class in Java.

Example:
```clojure
(require '[librarian.model.syntax :refer [defparadigm instanciate]]
		 '[my.concepts :refer [my-concept]]
		 '[my.other.concepts :refer [other-concept]]
		 '[my.other.paradigms :refer [parent-paradigm]])

(defparadigm my-paradigm [parent-paradigm]
  :concepts {:my-concept my-concept
             :other-concept other-concept}
  :builtins [(instanciate my-concept
			   :y (instanciate other-concept
				    :foo "bar"))])
```
In this example a paradigm with two concepts is created.
Instances of both concepts are defined as builtins.
See the docstring of the `instanciate` function for details on how it works.

**Note:** All instances described via `instanciate` will be processed via the correspondig concept's `:preprocess` and `:postprocess` functions as well as validated via `:spec` to prevent the addition of inconsistent or invalid builtins.
The same processing and validation steps are also performed by the [scraper](../scraper).

The concept aliases `:my-concept` and `:other-concept` assign a shorthand name for each concept that is relevant in a particular paradigm.
Aliases simplify the specification of [scraper configurations](../scraper) and [initial generator states](../generator).
Without aliases, each concept would have to be referenced using its fully qualified name, e.g. `:my.concepts/my-concept`.
Using aliases one can write `:my-concept` instead.
Another purpose of aliases is to simplify the attribute syntax:
```clojure
; Assuming my-concept has properties x, y; parent-concept has property z.
; The full attribute names would be:
:my.concepts/x :my.concepts/y :my.other.concepts/z
; If my-concept extends other-concept, instances have all three attributes.
; Attribute aliases simplify referring to those attributes:
:my-concept/x :my-concept/y :my-concept/z
; Thus the original place of definition of an attribute is not necessary to refer to it.
```

Lastly aliases are also useful to refer to a concept via multiple names, e.g. a `namespace` concept could be aliased to `:package` in a Java environment and to `:module` in a Python environment.

### 1.3. Defining Ecosystems 

```clojure
(defecosystem name [optional vector of parent paradigms or ecosystems ...]
  :concepts { ... A map of concept aliases to concepts ... }
  :builtins [ ... A collection of concept instance descriptions ... ]
  :generate A generator function
  :executor An executor factory)
```

Defines a new ecosystem with name `name` in the current namespace.
Similar to `defparadigm` but accepts additional key-value pair types:
-   `:generate`: A function that takes a metadata map and a database containing an executable CFG and that returns a snippet of executable code for the ecosystem.
-   `:executor`: Similar to the generator defined above but returns a function that executes the code snippet and returns the result of the execution instead of simply returning the code snippet string.

Ecosystems are essentially paradigms with support for some specific syntax via the `:generator` and `:executor` functions.
They typically also have a much more extensive set of `:builtins` than paradigms.

## 2. Builtin Model Components

The Librarian model comes with a collection of builtin components that are described next.

### 2.1. Builtin General-Purpose Concepts

Concepts that are useful across multiple programming paradigms and languages are defined in the [`librarian.model.concepts`](src/librarian/model/concepts) namespace.
Now follows an overview of those concepts.
For each concept the list of its attributes and parent concepts is given.

**Legend:**
-   **Derived** attributes will be automatically computed via a preprocessor or postprocessor and should not be manually provided.
-   **Indexed** attributes allow a fast reverse lookup of the entities having a given attribute value.
	By default only the forward direction is indexed.
-   **Unique** attributes are *indexed* and also guarantee that a reverse lookup will find at most one entity for any given value.

<table>
	<!-- Names -->
	<!----------->
	<tr><th colspan=4>Names & Positions</th></tr>
	<tr>
		<th>Concept</th>
		<th>Attribute</th>
		<th>Type / Cardinality / Index</th>
		<th>Description</th>
	</tr>
	<!-- named -->
	<tr>
		<td colspan=3>
			<code>named</code>
		</td>
		<td>
			A named entity.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>name</code></td>
		<td>String, indexed</td>
		<td>Name of the entity.</td>
	</tr>
	<!-- namespace -->
	<tr>
		<td colspan=3>
			<code>namespace</code> extends <code>named</code>
		</td>
		<td>
			A namespace.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>id</code></td>
		<td>Derived from <code>name</code>, unique</td>
		<td>Unique id of the namespace.</td>
	</tr>
	<tr>
		<td></td>
		<td><code>member</code></td>
		<td>Ref to <code>namespaced</code>, multiple (0..n), indexed</td>
		<td>A member of the namespace.</td>
	</tr>
	<!-- namespaced -->
	<tr>
		<td colspan=3>
			<code>namespaced</code> extends <code>named</code>
		</td>
		<td>
			A namespace member.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>id</code></td>
		<td>Derived vector, unique</td>
		<td>Fully-qualified name of the member: <code>[namespace-name member-name]</code></td>
	</tr>
	<!-- positionable -->
	<tr>
		<td colspan=3>
			<code>positionable</code>
		</td>
		<td>
			Something with an ordinal position.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>position</code></td>
		<td>Integer, optional (0..1)</td>
		<td>Ordinal position of the entity.</td>
	</tr>
	<!-- Types -->
	<!----------->
	<tr><th colspan=4>Datatypes</th></tr>
	<tr>
		<th>Concept</th>
		<th>Attribute</th>
		<th>Type / Cardinality / Index</th>
		<th>Description</th>
	</tr>
	<!-- datatype -->
	<tr>
		<td colspan=3>
			<code>datatype</code>
		</td>
		<td>
			A datatype.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>datatype</code></td>
		<td>Ref to <code>datatype</code>, multiple (0..n), indexed</td>
		<td>A supertype of the datatype.</td>
	</tr>
	<!-- basetype -->
	<tr>
		<td colspan=3>
			<code>basetype</code> extends <code>named</code>, <code>datatype</code>
		</td>
		<td>
			A basic type (like <code>int</code> or <code>boolean</code>).
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>id</code></td>
		<td>Derived from <code>name</code>, unique</td>
		<td>Unique id of the basetype.</td>
	</tr>
	<!-- semantic-type -->
	<tr>
		<td colspan=3>
			<code>semantic-type</code> extends <code>positionable</code>, <code>datatype</code>
		</td>
		<td>
			A datatype representing all values that have a certain semantic.
			Semantic types are fuzzy since their semantic is described via natural language.
			They can be ordered via <code>position</code> if the semantic <code>value</code>s for a <code>key</code> represent some sequence, e.g. a sequence of paragraphs.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>key</code></td>
		<td>String, optional (0..1)</td>
		<td>A context for the semantic <code>value</code>, e.g. <i>"description"</i> or <i>"unit"</i></td>
	</tr>
	<tr>
		<td></td>
		<td><code>value</code></td>
		<td>String</td>
		<td>A string describing the semantics of the type.</td>
	</tr>
	<!-- role-type -->
	<tr>
		<td colspan=3>
			<code>role-type</code> extends <code>datatype</code>
		</td>
		<td>
			Role types represent the set of values that can take a certain role.
			The role type with id <code>:dataset</code> for example could represent all training dataset arrays.
			While role types describe some kind of semantic, similar to <code>semantic-type</code>, they are not fuzzy and are assumed to have a clearly defined meaning. 
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>id</code></td>
		<td>Keyword, unique</td>
		<td>Unique id of the role type.</td>
	</tr>
	<!-- typed -->
	<tr>
		<td colspan=3>
			<code>typed</code>
		</td>
		<td>
			A concept with datatypes.
			The datatype of an entity with multiple types is the union type of those types.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>datatype</code></td>
		<td>Ref to <code>datatype</code>, multiple (0..n), indexed</td>
		<td>A datatype of the concept.</td>
	</tr>
	<!-- Callables -->
	<!----------->
	<tr><th colspan=4>Callables</th></tr>
	<tr>
		<th>Concept</th>
		<th>Attribute</th>
		<th>Type / Cardinality / Index</th>
		<th>Description</th>
	</tr>
	<!-- callable -->
	<tr>
		<td colspan=3>
			<code>callable</code> extends <code>typed</code>
		</td>
		<td>
			Represents something that can be called with parameters and returns results.
			It is typed so that semantic and role information can be attached to it.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>parameter</code></td>
		<td>Ref to <code>parameter</code>, multiple (0..n), indexed</td>
		<td>A parameter of the callable.</td>
	</tr>
	<tr>
		<td></td>
		<td><code>result</code></td>
		<td>Ref to <code>result</code>, multiple (0..n), indexed</td>
		<td>A returned result of the callable.</td>
	</tr>
	<!-- io-container -->
	<tr>
		<td colspan=3>
			<code>io-container</code> extends <code>named</code>, <code>typed</code>, <code>positionable</code>
		</td>
		<td>
			Represents an input or output (parameter or result) of a callable.
		</td>
	</tr>
	<tr>
		<td colspan=3></td>
		<td colspan=2><i>No attributes.</i></td>
	</tr>
	<!-- parameter -->
	<tr>
		<td colspan=3>
			<code>parameter</code> extends <code>io-container</code>, <code>data-receiver</code>
		</td>
		<td>
			Represents a parameter of a callable.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>optional</code></td>
		<td>Boolean, optional (<i>default: <code>false</code></i>)</td>
		<td>Denotes whether this parameter is optional.</td>
	</tr>
	<!-- result -->
	<tr>
		<td colspan=3>
			<code>result</code> extends <code>io-container</code>, <code>data-receiver</code>
		</td>
		<td>
			Represents a returned result of a callable.
		</td>
	</tr>
	<tr>
		<td colspan=3></td>
		<td colspan=2><i>No attributes.</i></td>
	</tr>
	<!-- Calls -->
	<!----------->
	<tr><th colspan=4>Control Flow Graph Nodes</th></tr>
	<tr>
		<th>Concept</th>
		<th>Attribute</th>
		<th>Type / Cardinality / Index</th>
		<th>Description</th>
	</tr>
	<!-- call -->
	<tr>
		<td colspan=3>
			<code>call</code> extends <code>typed</code>
		</td>
		<td>
			Represents a call to some <code>callable</code>.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>callable</code></td>
		<td>Ref to <code>callable</code></td>
		<td>The callable of this call.</td>
	</tr>
	<tr>
		<td></td>
		<td><code>parameter</code></td>
		<td>Ref to <code>call-parameter</code>, multiple (0..n), indexed</td>
		<td>A parameter of this call.</td>
	</tr>
	<tr>
		<td></td>
		<td><code>result</code></td>
		<td>Ref to <code>call-result</code>, multiple (0..n), indexed</td>
		<td>A result of this call.</td>
	</tr>
	<!-- data-receivable -->
	<tr>
		<td colspan=3>
			<code>data-receivable</code>
		</td>
		<td>
			Something that can be received by a <code>data-receiver</code>.
		</td>
	</tr>
	<tr>
		<td colspan=3></td>
		<td colspan=2><i>No attributes.</i></td>
	</tr>
	<!-- data-receiver -->
	<tr>
		<td colspan=3>
			<code>data-receiver</code> extends <code>data-receivable</code>
		</td>
		<td>
			A concept that can receive a value from some <code>data-receivable</code>.
			A receiver either has or receives some value to which it can optionally also get some additional semantic information from the outside.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>receives</code></td>
		<td>Ref to <code>data-receivable</code>, multiple (0..n), indexed</td>
		<td>A receivable from which this receiver gets its value and thus has to be able to accept the datatype of the received value.</td>
	</tr>
	<tr>
		<td></td>
		<td><code>receives-semantic</code></td>
		<td>Ref to <code>data-receivable</code>, multiple (0..n), indexed</td>
		<td>A receivable from which this receiver gets the <code>semantic-type</code>s of the value it holds.</td>
	</tr>
	<!-- call-parameter -->
	<tr>
		<td colspan=3>
			<code>call-parameter</code> extends <code>typed</code>, <code>positionable</code>, <code>data-receiver</code>
		</td>
		<td>
			Represents a parameter of a <code>call</code>.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>parameter</code></td>
		<td>Ref to <code>parameter</code></td>
		<td>The <code>parameter</code> for which this <code>call-parameter</code> provides a value.</td>
	</tr>
	<!-- call-result -->
	<tr>
		<td colspan=3>
			<code>call-result</code> extends <code>typed</code>, <code>positionable</code>, <code>data-receiver</code>
		</td>
		<td>
			Represents a result of a <code>call</code>.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>result</code></td>
		<td>Ref to <code>result</code></td>
		<td>The <code>result</code> that provides the value for this <code>call-result</code>.</td>
	</tr>
	<!-- constant -->
	<tr>
		<td colspan=3>
			<code>constant</code> extends <code>typed</code>, <code>datatype</code>, <code>data-receivable</code>
		</td>
		<td>
			Represents a constant value that can be received by <code>call-parameters</code>.
			The constant concept is implemented as a typed datatype, where a constant is its own instance.
			This was done to be able to represent enum types as disjunctions of constants <i>(disjunctions are however not yet supported)</i>.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>value</code></td>
		<td>String or integer or boolean</td>
		<td>The value of the constant.</td>
	</tr>
	<!-- snippet -->
	<tr>
		<td colspan=3>
			<code>snippet</code>
		</td>
		<td>
			Represents a code snippet/template as a partial CFG.
			A snippet is a concept that points to the CFG nodes that make up its partial CFG.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>value</code></td>
		<td>Ref to a CFG-node or any concept with a truthy <code>:placeholder</code> attribute</td>
		<td>A control-flow concept that is part of the snippet.</td>
	</tr>
</table>

### 2.2. Builtin Paradigms

The model comes with three builtin paradigms.

#### 2.2.1. The `common` paradigm

A universal paradigm of concepts that are common in many paradigms.

**Concept Aliases:**
-   `:named`: `named`
-   `:namespace`: `namespace`
-   `:namespaced`: `namespaced`
-   `:datatype`: `datatype`
-   `:basetype`: `basetype`
-   `:semantic-type`: `semantic-type`
-   `:role-type`: `role-type`
-   `:typed`: `typed`
-   `:callable`: `callable`
-   `:io-container`: `io-container`
-   `:parameter`: `parameter`
-   `:result`: `result`
-   `:call`: `call`
-   `:data-receiver`: `data-receiver`
-   `:call-parameter`: `call-parameter`
-   `:call-result`: `call-result`
-   `:constant`: `constant`
-   `:snippet`: `snippet`

No additional concepts or builtin instances are defined.

#### 2.2.2. The `functional` paradigm (extends `common`)

A paradigm for functional languages.

**Additional Concept Aliases:**
-   `:function`: `function`

<table>
	<tr><th colspan=4>Functional Concepts</th></tr>
	<tr>
		<th>Concept</th>
		<th>Attribute</th>
		<th>Type / Cardinality / Index</th>
		<th>Description</th>
	</tr>
	<!-- function -->
	<tr>
		<td colspan=3>
			<code>function</code> extends <code>namespaced</code>, <code>callable</code>
		</td>
		<td>
			A function.
		</td>
	</tr>
	<tr>
		<td colspan=3></td>
		<td colspan=2><i>No attributes.</i></td>
	</tr>
</table>

No builtin instances are defined.

#### 2.2.3. The `oo` paradigm (extends `common`)

A paradigm for object oriented languages.

**Additional Concept Aliases:**
-   `:class`: `class`
-   `:constructor`: `constructor`
-   `:method`: `method`

<table>
	<tr><th colspan=4>OOP Concepts</th></tr>
	<tr>
		<th>Concept</th>
		<th>Attribute</th>
		<th>Type / Cardinality / Index</th>
		<th>Description</th>
	</tr>
	<!-- class -->
	<tr>
		<td colspan=3>
		<code>class</code> extends <code>typed</code>, <code>namespaced</code>, <code>datatype</code>
		</td>
		<td>
			A class.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>constructor</code></td>
		<td>Ref to <code>constructor</code>, multiple (1..n), indexed</td>
		<td>Constructor of the class.</td>
	</tr>
	<tr>
		<td></td>
		<td><code>method</code></td>
		<td>Ref to <code>method</code>, multiple (0..n), indexed</td>
		<td>Method of the class.</td>
	</tr>
	<!-- constructor -->
	<tr>
		<td colspan=3>
		<code>constructor</code> extends <code>callable</code>
		</td>
		<td>
			A constructor of a class.
		</td>
	</tr>
	<tr>
		<td colspan=3></td>
		<td colspan=2><i>No attributes.</i></td>
	</tr>
	<!-- method -->
	<tr>
		<td colspan=3>
		<code>method</code> extends <code>named</code>, <code>callable</code>
		</td>
		<td>
			A method of a class.
		</td>
	</tr>
	<tr>
		<td colspan=3></td>
		<td colspan=2><i>No attributes.</i></td>
	</tr>
</table>

No builtin instances are defined.

### 2.3. Builtin Ecosystems

The model provides its builtin ecosystems via the [`librarian.model.core/ecosystems`](src/librarian/model/core.clj) map:
```clojure
{:python python}
```
Every ecosystem has a keyword alias with which it can be referenced in scraper configuration files.

Currently only an ecosystem for Python (`:python`) is provided.

#### 2.3.1. The `python` ecosystem (extends `functional`, `oo`)

An ecosystem for Python.

**Additional Concept Aliases:**
-   `:class`: `python/class` (overrides `class`)
-   `:constructor`: `python/constructor` (overrides `constructor`)
-   `:basetype`: `python/basetype` (overrides `basetype`)

<table>
	<tr><th colspan=4>Python Concepts</th></tr>
	<tr>
		<th>Concept</th>
		<th>Attribute</th>
		<th>Type / Cardinality / Index</th>
		<th>Description</th>
	</tr>
	<!-- class -->
	<tr>
		<td colspan=3>
		<code>python/class</code> extends <code>class</code>
		</td>
		<td>
			A Python class.
			Like <code>class</code> but can only have a single constructor and automatically recognizes methods named <code>__init__</code> as its constructor.
		</td>
	</tr>
	<tr>
		<td colspan=3></td>
		<td colspan=2><i>No attributes.</i></td>
	</tr>
	<!-- constructor -->
	<tr>
		<td colspan=3>
		<code>python/constructor</code> extends <code>constructor</code>
		</td>
		<td>
			Like <code>constructor</code> but with a unique reference to its class.
		</td>
	</tr>
	<tr>
		<td></td>
		<td><code>class</code></td>
		<td>Derived ref to <code>python/class</code>, unique</td>
		<td>A reference to the constructor's class. In Python this uniquely identifies a constructor.</td>
	</tr>
	<!-- basetype -->
	<tr>
		<td colspan=3>
		<code>python/basetype</code> extends <code>basetype</code>
		</td>
		<td>
			Like <code>basetype</code> but only allows the Python basetype names:
			<i>"object", "int", "float", "complex", "string", "boolean"</i>.
		</td>
	</tr>
	<tr>
		<td colspan=3></td>
		<td colspan=2><i>No attributes.</i></td>
	</tr>
</table>

**Builtin Instances:**
-   `basetype` instances: `int`, `float`, `complex`, `string`, `boolean` which all extend `object`.
-   Typecasting `function`s:
	-   `str(x)`: `object -> string`
	-   `int(x)`: `object -> int`
	-   `float(x)`: `object -> float`

Other Python builtins can be added when needed.
