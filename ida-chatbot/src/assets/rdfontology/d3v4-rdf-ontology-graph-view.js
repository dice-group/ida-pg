function createV4RDFOntologyGraph(figId, svgId, fileName,) {
  var linksArray = [];
  var nodesArray = [];

  var width = 1060,height = 900;


  var svg = d3.select("#"+svgId)
    .append("svg")
    .attr("width", width)
    .attr("height", height);
 
  svg.append("svg:defs").selectAll("marker")
  .data(["end"])
  .enter().append("svg:marker")
  .attr("id", String)
  .attr("viewBox", "0 -5 15 15")
  .attr("refX", 30) 
  .attr("refY", -0.5)
  .attr("markerWidth", 26)
  .attr("markerHeight", 6)
  .attr("orient", "auto")
  .append("svg:polyline")
  .attr("points", "0,-5 10,0 0,5")
  ;
  

  var color = d3.scaleOrdinal(d3.schemeCategory20);

  var simulation = d3.forceSimulation()
    .force("link", d3.forceLink().id(function(d) { return d.id; }).distance(50))
    .force("charge", d3.forceManyBody().strength(-1000)) //Charge strength is here
    .force("center", d3.forceCenter((width / 2), (height / 2)));
  
  d3.json("assets/rdfontology/historian-onlogoly.json", function(error, data) 
  {
    var literalIndex = 0;
    var circleIdIndex = 0;
    data["@graph"].forEach(function(element) 
    {
        var tempSub ="";
        var tempPred ="";
        var tempObj ="";
        var pred="---subClassOf---";
        var tempDomainArray = [];
        //START :: Nodes and Links creation for subClassOf
        if(element["rdfs:subClassOf"] != undefined)
        {
          
          var tempLabelArray = element["@id"].split('/');
          tempSub = tempLabelArray[tempLabelArray.length -1 ];
          
          if(Object.prototype.toString.call(element["rdfs:subClassOf"]) === '[object Array]')
          {
            element["rdfs:subClassOf"].forEach(function(eachIds)
            {  
              var tempLabelArray = eachIds["@id"].split('/');
              tempObj = tempLabelArray[tempLabelArray.length -1 ];

              var tripleValue = {source:tempSub, 	predicate:pred, 	target:tempObj,value: 1};
              linksArray.push(tripleValue);
              createNewNode(tempSub,pred,tempObj);
            });
          }
          else
          {
            var tempLabelArray = element["rdfs:subClassOf"]["@id"].split('/');
            tempObj = tempLabelArray[tempLabelArray.length -1 ];
            var tripleValue = {source:tempSub, 	predicate:pred, 	target:tempObj,value: 1};
            linksArray.push(tripleValue);

            createNewNode(tempSub,pred,tempObj);
          }
            
        }
        //END :: Nodes and Links creation for subClassOf
        
      });
      
      if (error) {
          return console.warn('Error in createV3RDFOntologyGraphView() method :'+error);
      }
      
      var g = svg.append("g")
          .attr("class", "everything");         
        
    var link = g.append("g")
          .attr("class", "links")
          .selectAll("line")
          .data(linksArray)
          .enter().append("line")
          .attr("class", "link")
          .attr("stroke-width",1)
          .style("fill","#999")
          .attr("marker-end", "url(#end)")
          .style("stroke-dasharray",function (d) {return  ("0, 0");})
          ;

          // ==================== Add Link Names =====================
    var linkTexts = g.selectAll(".link-text")
            .data(linksArray)
            .enter()
            .append("text")
              .attr("class", "link-text")
              .text( function (d) { 
                //console.log(d);
                return d.predicate; }) 
                .style("fill",function(d){return "#28B463";})
                .style("font-size", "80%")
                .style("font-style", "italic")
                .attr('x', 12)
                .attr('y', 3)
    ;           
    // ==================== Add Link Names =====================

    var node = g.append("g")
          .attr("class", "nodes")
        .selectAll("g")
        .data(nodesArray)
        .enter().append("g")
    ;    
    var circles = node.append("circle")
          .attr("id",function(d){return d.nodeId ;})
          .attr("r","8")
          .style("fill",function(d){ return "#311B92";})
          .call(d3.drag()
              .on("start", dragstarted)
              .on("drag", dragged)
             .on("end", dragended)
    );

    var lables = node.append("text")
              .attr("class", "node-text")
              .text(function (d) { return d.id;  })
              .style("font-size","100%")
              .style("fill","#311B92")
              .attr('x', 12)
              .attr('y', 3)
    ;

    simulation
          .nodes(nodesArray)
          .on("tick", ticked)
    ;

    simulation.force("link")
          .links(linksArray)
    ;

    function ticked()
    {
      lables
          .attr("x2", function(d){ return d.x-width; })
          .attr("y2", function (d) {return d.y -height; })
      ;
      link
          .attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; })
      ;
      linkTexts
          .attr("x", function(d) { //return  Math.max(5, Math.min(width - 10, (d.source.x + d.target.x)/2 ));
            return 4 + (d.source.x + d.target.x)/2  ; })
          .attr("y", function(d) { 
            return 4 + (d.source.y + d.target.y)/2 ; })
      ;
      node
        .attr("transform", function(d) {
            return "translate(" + d.x + "," + d.y + ")";
          })
      ;
    }

    function createNewNode(tempSub,tempPred,tempObj)
    {
      //START :: Node creation
      var subNodeAdded = false;  
      var objNodeAdded = false;
        
      if(nodesArray != undefined)
      {
        nodesArray.forEach(function(elem) 
        {
          if(elem["id"] === tempSub)
          {
            subNodeAdded = true;
          } 
          if(elem["id"] === tempObj)
          {
            objNodeAdded = true;
          } 
        });
      }
      
      if(subNodeAdded === false)
      {
        var nodeValue = {id:tempSub ,predicate:tempPred, group: 1};
        nodesArray.push(nodeValue);
      }
      if(objNodeAdded === false)
      {
        var nodeValue = {id:tempObj ,predicate:tempPred, group: 1};
        nodesArray.push(nodeValue);
      }
      //END :: Node creation
      //console.log("nodeValue len =>"+nodesArray.length);
    }
  });

  function dragstarted(d) {
    if (!d3.event.active) simulation.alphaTarget(0.3).restart();
    d.fx = d.x;
    d.fy = d.y;
  }

  function dragged(d) {
    d.fx = d3.event.x;
    d.fy = d3.event.y;
  }

  function dragended(d) {
    if (!d3.event.active) simulation.alphaTarget(0);
    d.fx = null;
    d.fy = null;
  }
};
