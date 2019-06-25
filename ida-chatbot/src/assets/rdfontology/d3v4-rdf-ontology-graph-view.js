function createV4RDFOntologyGraph(figId, svgId, fileName,displayDeustch,displaySubclasses,dispalyAllprop,) {
  var linksArray = [];
  var nodesArray = [];
  var enLabelArray = [];
  var deLabelArray = [];

  var width = 1000,height = 900;

  var svgClear = d3.select("#"+svgId);
  svgClear.selectAll("*").remove(); 
  
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
    //START :: get english and deutsch labels values into arrays
    data["@graph"].forEach(function(elem) 
    {
      if(elem["rdfs:label"] != undefined)
      {
        if(Object.prototype.toString.call(elem["rdfs:label"]) === '[object Array]')
        {
          elem["rdfs:label"].forEach(function(eachIds)
          {  
            if(eachIds["@language"] == "de")
            {
              var tempLabel = {"id":elem["@id"],"language":"de","value":eachIds["@value"]}
              deLabelArray.push(tempLabel);
            }
            if(eachIds["@language"] == "en")
            {
              var tempLabel = {"id":elem["@id"],"language":"en","value":eachIds["@value"]}
              enLabelArray.push(tempLabel);
            }

          });
        }
        else
        {
          if(elem["rdfs:label"]["@language"] == "de")
          {
            var tempLabel = {"id":elem["@id"],"language":"de","value":elem["rdfs:label"]["@value"]}
            deLabelArray.push(tempLabel);
          }
          if(elem["rdfs:label"]["@language"] == "en")
          {
            var tempLabel = {"id":elem["@id"],"language":"en","value":elem["rdfs:label"]["@value"]}
            enLabelArray.push(tempLabel);
          }
        }
      }
        
    });
    //END :: get english and deutch labels values into arrays

    //START :: Creation of Graph depenging on displaySubclasses and dispalyAllprop values
    var literalIndex = 0;
    var circleIdIndex = 0;
    data["@graph"].forEach(function(element) 
    {
        var tempSub ="?";
        var tempPred ="?";
        var tempObj ="?";
        var pred="---subClassOf---";
        var tempDomainArray = [];
        //START :: Nodes and Links creation for subClassOf
        if(displaySubclasses)
        {
            if(element["rdfs:subClassOf"] != undefined)
            {
              tempSub = getLabel(displayDeustch,element["@id"]);
              if(tempSub == "noLabel")
              {
                var tempLabelArray = element["@id"].split('/');
                tempSub = tempLabelArray[tempLabelArray.length -1 ]+'?';
              }

              if(Object.prototype.toString.call(element["rdfs:subClassOf"]) === '[object Array]')
              {
                element["rdfs:subClassOf"].forEach(function(eachIds)
                {  
                  tempObj = getLabel(displayDeustch,eachIds["@id"]);
                  if(tempObj == "noLabel")
                  {
                    var tempLabelArray = eachIds["@id"].split('/');
                    tempObj = tempLabelArray[tempLabelArray.length -1 ]+'?';
                  }

                  var tripleValue = {source:tempSub, 	predicate:pred, 	target:tempObj,value: 1};
                  linksArray.push(tripleValue);
                  createNewNode(tempSub,pred,tempObj);
                });
              }
              else
              {
                tempObj = getLabel(displayDeustch,element["rdfs:subClassOf"]["@id"]);
                if(tempObj == "noLabel")
                {
                  var tempLabelArray = element["rdfs:subClassOf"]["@id"].split('/');
                  tempObj = tempLabelArray[tempLabelArray.length -1 ]+'?';
                }
                //var tripleValue = {source:tempSub, 	predicate:'---subClassOf---', 	target:tempObj,value: 1};
                var tripleValue = {source:tempSub, 	predicate:pred, 	target:tempObj,value: 1};
                linksArray.push(tripleValue);

                //createNewNode(tempSub,'---subClassOf---',tempObj);
                createNewNode(tempSub,pred,tempObj);
              }
                
            }
        }
        //END :: Nodes and Links creation for subClassOf

        //START :: Nodes and Links creation for all Properties
        if(dispalyAllprop)
        {
            //get the subject label  
            if(element["rdfs:domain"]!=undefined)
            {
              if(Object.prototype.toString.call(element["rdfs:domain"]) === '[object Array]'){
                //console.log("inside if");
                element["rdfs:domain"].forEach(function(eachIds)
                {
                  var tempDomainLabel = getLabel(displayDeustch,eachIds["@id"]);
                  if(tempDomainLabel == "noLabel")
                  {
                    var tempLabelArray = eachIds["@id"].split('/');
                    tempDomainLabel = tempLabelArray[tempLabelArray.length -1 ]+'?';
                  }
                  tempDomainArray.push(tempDomainLabel);
                });
              }
              else
              {
                tempSub = getLabel(displayDeustch,element["rdfs:domain"]["@id"]);
                if(tempSub == "noLabel")
                {
                  var tempLabelArray = element["rdfs:domain"]["@id"].split('/');
                  tempSub = tempLabelArray[tempLabelArray.length -1 ]+'?';
                }
              }
            }

            //get the predicate
            if(element["rdfs:domain"]!=undefined)
            {
              tempPred = getLabel(displayDeustch,element["@id"]);
              if(tempPred == "noLabel")
              {
                var tempLabelArray = element["@id"].split('/');
                tempPred = tempLabelArray[tempLabelArray.length -1 ]+'?';
              }
            }


            //get the object  range
            if(element["rdfs:range"]!=undefined){
              //console.log(element.domain["@id"]);
              if ("@id" in element["rdfs:range"])
              {
                tempObj = getLabel(displayDeustch,element["rdfs:range"]["@id"]);
                if(tempObj == "noLabel")
                {
                  var tempLabelArray = element["rdfs:range"]["@id"].split('/');
                  tempObj = tempLabelArray[tempLabelArray.length -1 ]+'?';
                }
              }  
            }
            
            if(tempObj.toLowerCase().includes("string") || tempObj.toLowerCase().includes("boolean")|| tempObj.toLowerCase().includes("integer") || tempObj.toLowerCase().includes("date") || tempObj.toLowerCase().includes("time"))
            {
              tempObj = tempObj + literalIndex;
              literalIndex = literalIndex + 1 ;
            }

            if(!tempDomainArray.length  ==0)
            {
              tempDomainArray.forEach(function(el)
              {
                if(el != "?" && tempPred != "?" && tempObj != "?")
                {
                  var tripleValue = {source:el, 	predicate:tempPred, 	target:tempObj,value: 1};
                  linksArray.push(tripleValue);
                  createNewNode(el,tempPred,tempObj);
                }
              });
            }
            else
            {
              if(tempSub != "?" && tempPred != "?" && tempObj != "?")
              {
                var tripleValue = {source:tempSub, 	predicate:tempPred, 	target:tempObj,value: 1};
                linksArray.push(tripleValue);
                createNewNode(tempSub,tempPred,tempObj);
              }
            }
        }
        //END :: Nodes and Links creation for all Properties
        
        /*
        if(element.subPropertyOf != undefined){
          tempSub = element.label.value;
          
          tempObj = element.subPropertyOf["@id"];
            data["@graph"].forEach(function(elem) {
              //console.log(elem["@id"]);
              if(elem["@id"] == element.subPropertyOf["@id"]){
                if(elem.label != undefined){
                  
                tempObj = elem.label.value;
                //console.log("elem.label.value => "+elem.label.value);
                }
              }
            });
          var tripleValue = {subject:tempSub, 	predicate:'---subPropertyOf---', 	object:tempObj};
          triples.push(tripleValue);
          
        }
          */
        //}
      });
      //END :: Creation of Graph depenging on displaySubclasses and dispalyAllprop values

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
          .style("stroke-dasharray",function (d) { 
            var labelStr = d.predicate; 
            if(labelStr.includes('subClassOf')){return  ("3, 3");}
            if(labelStr.includes('subPropertyOf')){return  ("5, 5");}
            return  ("0, 0");})
          
          ;

          // ==================== Add Link Names =====================
    var linkTexts = g.selectAll(".link-text")
            .data(linksArray)
            .enter()
            .append("text")
              .attr("class", "link-text")
              .text( function (d) { 
                return d.predicate; }) 
                .style("fill",function(d){var labelStr = d.predicate; 
                  if(labelStr.includes('subClassOf'))
                  {return "#3498DB";}
                  if(labelStr.includes('subPropertyOf'))
                  {return "#85929E";}
                  return "#28B463";})
                .style("font-size", "80%")
                .style("font-style", "italic")
                .attr('x', 12)
                .attr('y', 3)
    ;        

    linkTexts.append("title")
              .text(function(d) { var returnValue = 'Source : '+d.source +'\nProperty : '+d.predicate+'\nObject : '+d.target;
                return returnValue;
              });
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
          .attr("r",function(d){var labelStr = d.id; 
            if(labelStr.toLowerCase().includes('string')|| labelStr.toLowerCase().includes('integer')|| labelStr.toLowerCase().includes('datetime')|| labelStr.toLowerCase().includes('boolean'))
            {return 6;}
            return 10;})
          .style("fill",function(d){var labelStr = d.id; 
            if(labelStr.toLowerCase().includes('string')|| labelStr.toLowerCase().includes('integer')|| labelStr.toLowerCase().includes('datetime')|| labelStr.toLowerCase().includes('boolean'))
                {return "#FF9800";} 
            return "#311B92";})
          .call(d3.drag()
              .on("start", dragstarted)
              .on("drag", dragged)
             .on("end", dragended)
    );

    var lables = node.append("text")
              .attr("class", "node-text")
              .text(function (d) {
                var labelStr = d.id; 
                if(labelStr.toLowerCase().includes('string')){return "String";}
                if(labelStr.toLowerCase().includes('datetime')){return "DateTime";}
                if(labelStr.toLowerCase().includes('integer')){return "Integer";}
                if(labelStr.toLowerCase().includes('boolean')){return "Boolean";}
                return d.id; 
              })
              .style("font-size", function(d){var labelStr = d.id; 
                if(labelStr.toLowerCase().includes('string')|| labelStr.toLowerCase().includes('integer')|| labelStr.toLowerCase().includes('datetime')|| labelStr.toLowerCase().includes('boolean'))
                {return "80%";}
                return "100%";})
              .style("font-style", function(d){var labelStr = d.id; 
                if(labelStr.toLowerCase().includes('string')|| labelStr.toLowerCase().includes('integer')|| labelStr.toLowerCase().includes('datetime')|| labelStr.toLowerCase().includes('boolean'))
                {return "italic";}
                return "bold";})
              .style("fill",function(d){var labelStr = d.id; 
                if(labelStr.toLowerCase().includes('string')|| labelStr.toLowerCase().includes('integer')|| labelStr.toLowerCase().includes('datetime')|| labelStr.toLowerCase().includes('boolean'))
                    {return "#FF9800";}
                return "#311B92";})
              .attr('x', 12)
              .attr('y', 3)
    ;

    node.append("title")
          .text(function(d) { 
            var returnValue = 'Node : '+d.id;
            return returnValue; })
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
          .attr("x", function(d) { 
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
    function getLabel(displayDeustch,id)
    {
      var returnVaule = "noLabel"
      if(!displayDeustch)
      {
        enLabelArray.forEach(function(elem) 
        {
          if(elem["id"] === id)
          {
            returnVaule = elem["value"];
          } 
        });
      }
      else if(displayDeustch)
      {
        deLabelArray.forEach(function(elem) 
        {
          if(elem["id"] === id)
          {
            returnVaule = elem["value"];
          } 
        });
      }
      return returnVaule;
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
