function createV3RDFOntologyView() {
    var margin = {top: 30, right: 20, bottom: 30, left: 20},
    width = 1200 - margin.left - margin.right,
    barHeight = 40,
    barWidth = width * .25;

var i = 0,
    duration = 400,
    root,
    childrenFinder;

var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.y, d.x]; });
	
var tree = d3.layout.tree()
    .size([0, 100]);




var svg = d3.select("#svgid")
    .attr("width", width + margin.left + margin.right)
  .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

/*svg.append("rect")
    .attr("width", "100%")
    .attr("height", "100%")
    //.attr("fill", "hsl(" + Math.random() * 56.5 + ", 90%, 50%)");
	.attr("fill", "#9e7777");
	*/
d3.json("assets/rdfontology/historianOntology.json", function(error, flare) {
  flare.x0 = 0;
  flare.y0 = 0;
  flare["@id"] = "owl:Thing";
  flare.label = new Array();
  flare.label.value = "owl:Thing";
  root = flare;
  childrenFinder = function(node) {
            var j = 0;
            var children = new Array();
            var id = node["@id"];
            if (id!==undefined) {
              root["@graph"].forEach(function(n, i) {
			    if(n!==undefined)
                if (n.subClassOf!==undefined)
                if ("@id" in n.subClassOf)
                if (n.subClassOf["@id"]===id){
                    children[j] = n; j++;
                }
                }
              );    
            }
            return children;
          };
// method which find the children of a node    
  tree.children( childrenFinder)
  tree.nodes(root); // add the trre structure with the childrenFinder fct
  tree.children(function children(d) {  return d.children; }); // restore the standard walk in the tree
  update(flare);
});

function update(source) {
  // Compute the flattened node list. TODO use d3.layout.hierarchy.
  var nodes = tree.nodes(root);
  j=0;

  var height = Math.max(500, nodes.length * barHeight + margin.top + margin.bottom);

  d3.select("svg")
      .attr("height", height);

  d3.select(self.frameElement)
      .style("height", height + "px");

  // Compute the "layout".
  nodes.forEach(function(n, i) {
    n.x = i * barHeight;
  });

  // Update the nodes…
  var node = svg.selectAll("g.node")
      .data(nodes, function(d) { 
          return d.id || (d.id = ++i); 
          });

  var nodeEnter = node.enter().append("g")
      .attr("class", "node")
      .attr("transform", function(d) { 
          translation = "translate(" + source.y0 + "," + source.x0 + ")";
          return translation;  
      })
      .style("opacity", 1e-10);

  // Enter any new nodes at the parent's previous position.
  nodeEnter.append("rect")
      .attr("y", -barHeight / 3)
	  //.attr("x", +100)
      .attr("height", barHeight-10)
      .attr("width", barWidth-10)
	  .attr("rx", 20)         // set the x corner curve radius
      .attr("ry", 20)
      .style("fill", color)
      .on("click", click);
  /*nodeEnter.append("ellipse")
                         .attr("cx", 50)
                         .attr("cy", -barHeight / 2)
                         .attr("rx", -barHeight / 2)
                         .attr("ry", 10);*/
  nodeEnter.append("text")
      .attr("dy", 5.5)
      .attr("dx", 55.5)
      .text(function(d) { 
          var label = (d.label!==undefined?d.label.value:"?");
          return label; 
        })
        .style("font-size", "80%")
		.attr("fill", "black");

  // Transition nodes to their new position.
  nodeEnter.transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })
      .style("opacity", 1);

  node.transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })
      .style("opacity", 1)
      .select("rect")
      .style("fill", color);
	   

  // Transition exiting nodes to the parent's new position.
  node.exit().transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
      .style("opacity", 1e-6)
      .remove();

  // Update the links…
  var link = svg.selectAll("path.link")
      .data(tree.links(nodes), function(d) { return d.target.id; })
	  

  // Enter any new links at the parent's previous position.
  link.enter().insert("path", "g")
      .attr("class", "link")
      .attr("d", function(d) {
        var o = {x: source.x0, y: source.y0};
        return diagonal({source: o, target: o});
      })
	.style("stroke","#cdd9a9")
	.style("fill", "none")
    .transition()
      .duration(duration)
      .attr("d", diagonal);

  // Transition links to their new position.
  link.transition()
      .duration(duration)
      .attr("d", diagonal);

  // Transition exiting nodes to the parent's new position.
  link.exit().transition()
      .duration(duration)
      .attr("d", function(d) {
        var o = {x: source.x, y: source.y};
        return diagonal({source: o, target: o});
      })
      .remove();

  // Stash the old positions for transition.
  nodes.forEach(function(d) {
    d.x0 = d.x;
    d.y0 = d.y;
  });
}

// Toggle children on click.
function click(d) {
  if (d.children) {
    d._children = d.children;
    d.children = null;
  } else {
    d.children = d._children;
    d._children = null;
  }
  update(d);
}

function color(d) {
  //return d._children ? "#d41b08" : d.children ? "#15730a" : "#9e9e9e";
  return d._children ? "#f17a07" : d.children ? "#d163a3" : "#9e9e9e";
  //return d._children ? "#f17a07" : d.children ? "#9e9e9e" : "#ffe7bc";
}
};
