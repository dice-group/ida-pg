# Generator Technical Documentation

## Basic idea

The generator works by performing an A\* search through the space of possible *control flow graphs* (CFGs).
It returns the cheapest solution it can find, where a solution is a CFG that represents a valid program, that can be converted into executable/compilable source code.

The search starts with an initial incomplete CFG that only contains nodes for known inputs and for required outputs.
A solution is found if for each call in the CFG the concrete callable (function or method) is known and each call parameter is assigned some value.

The search space is traversed via 5 classes of generator actions:
1. **Parameter filling:** Adds a `receives` edge from a call parameter to some value-providing node, i.e. either a call result or a constant value.
2. **Parameter removal:** Removes an optional call parameter.
3. **Call insertion:** Inserts a new call node, which represents a call to a method or function. 
	Attached to each call node there may be call parameter and result nodes.
4. **Snippet insertion:** Inserts an entire CFG as a subgraph into the current CFG. This speeds up the insertion of common coding patterns specific to a library.
5. **Call completion:** Completes a call node that is not fully specified yet, e.g. only the namespace and the return types of the called function/method are known but not the concrete callable.

The first two action classes reduce the number of flaws in a given CFG, i.e. they bring it closer to a solution state.
Action classes 3 and 4 increase the number of flaws in a given CFG since they introduce new potentially incomplete calls and call parameters that will have to be handled before a solution is found.
Action class 5 resolves incomplete call flaws by completing calls, which in turn might entail the insertion of new call parameters from the completed callable.

## Cost model and search heuristic

To steer the search the following cost model is used.
The cost of each solution path represents the probability of the solution fulfilling the users requirements.
Thus the path cost is the product of the edge/action probabilities.
This product should be maximized.

This optimization goal is modeled by minimizing the sum of the negative logarithm of the edge probabilities instead.
