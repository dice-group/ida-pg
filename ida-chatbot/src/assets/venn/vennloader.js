function renderVennDiagram(containerId, mainDivId, sets, ttPostfix){
  var containerNode = d3.select('#'+containerId).node();
  var chart = venn.VennDiagram()
    .width(containerNode.clientWidth)
    .height(containerNode.clientHeight);
  var mainDiv = d3.select("#"+mainDivId)
  mainDiv.datum(sets).call(chart);
  var tooltip = d3.select("#"+containerId).append("div")
    .attr("class", "venntooltip");
  mainDiv.selectAll("path")
    .style("stroke-opacity", 0)
    .style("stroke", "#fff")
    .style("stroke-width", 3)
  mainDiv.selectAll("g")
    .on("mouseover", function(d, i) {
      // sort all the areas relative to the current item
      venn.sortAreas(mainDiv, d);
      // Display a tooltip with the current size
      tooltip.transition().duration(400).style("opacity", .9);
      tooltip.text(d.size + " " + ttPostfix);
      // highlight the current path
      var selection = d3.select(this).transition("tooltip").duration(400);
      selection.select("path")
        .style("fill-opacity", d.sets.length == 1 ? .4 : .1)
        .style("stroke-opacity", 1);
    })
    .on("mousemove", function() {
      tooltip.style("left", (d3.event.pageX - 100) + "px")
        .style("top", (d3.event.pageY - 50) + "px");
    })
    .on("mouseout", function(d, i) {
      tooltip.transition().duration(400).style("opacity", 0);
      var selection = d3.select(this).transition("tooltip").duration(400);
      selection.select("path")
        .style("fill-opacity", d.sets.length == 1 ? .25 : .0)
        .style("stroke-opacity", 0);
    });
}



