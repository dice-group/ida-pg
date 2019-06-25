function createV4RDFOntologyGraph(figId, svgId, fileName,displayDeustch,displaySubclasses,
                                  dispalyAllprop,applyNodesBoundary,disableZoom,idsArray) {
  var linksArray = [];
  var nodesArray = [];
  var enLabelArray = [];
  var deLabelArray = [];
  var expandedNodesArray = [];

  var width = 1060,height = 900,resourceRadius = 8,literalRadius = 5;

  
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
  .style("fill","black")
  .attr("points", "0,-5 18,0 0,5")
  ;
  svg.append("svg:defs").selectAll("marker")
  .data(["end1"])
  .enter().append("svg:marker")
  .attr("id", String)
  .attr("viewBox", "0 -5 15 15")
  .attr("refX", 30) 
  .attr("refY", -0.5)
  .attr("markerWidth", 26)
  .attr("markerHeight", 6)
  .attr("orient", "auto")
  .append("svg:polyline")
  .style("fill", "grey")
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
                var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
                tempSub = tempLabelArray1[tempLabelArray1.length -1 ]+'?';
              }

              if(Object.prototype.toString.call(element["rdfs:subClassOf"]) === '[object Array]')
              {
                element["rdfs:subClassOf"].forEach(function(eachIds)
                {  
                  tempObj = getLabel(displayDeustch,eachIds["@id"]);
                  if(tempObj == "noLabel")
                  {
                    var tempLabelArray = eachIds["@id"].split('/');
                    var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
                    tempObj = tempLabelArray1[tempLabelArray1.length -1 ]+'?';
                  }

                  var tripleValue = {source:tempSub, 	predicate:pred, 	target:tempObj,value: 1};
                  linksArray.push(tripleValue);
                  
                  //createNewNode(tempSub,'---subClassOf---',tempObj);
                  createNewNode(tempSub,pred,tempObj,false,'',element);
                });
              }
              else
              {
                tempObj = getLabel(displayDeustch,element["rdfs:subClassOf"]["@id"]);
                if(tempObj == "noLabel")
                {
                  var tempLabelArray = element["rdfs:subClassOf"]["@id"].split('/');
                  var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
                  tempObj = tempLabelArray1[tempLabelArray1.length -1 ]+'?';
                }
                //var tripleValue = {source:tempSub, 	predicate:'---subClassOf---', 	target:tempObj,value: 1};
                var tripleValue = {source:tempSub, 	predicate:pred, 	target:tempObj,value: 1};
                linksArray.push(tripleValue);

                //createNewNode(tempSub,'---subClassOf---',tempObj);
                createNewNode(tempSub,pred,tempObj,false,'',element);
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
                    var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
                    tempDomainLabel = tempLabelArray1[tempLabelArray1.length -1 ]+'?';
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
                  var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
                  tempSub = tempLabelArray1[tempLabelArray1.length -1 ]+'?';
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
                var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
                tempPred = tempLabelArray1[tempLabelArray1.length -1 ]+'?';
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
                  var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
                  tempObj = tempLabelArray1[tempLabelArray1.length -1 ]+'?';
                }
              }  
            }
            
            var isLitteral = false;
            var literalDataType = tempObj;

            if(tempObj.toLowerCase().includes("string") || tempObj.toLowerCase().includes("boolean")|| tempObj.toLowerCase().includes("integer") || tempObj.toLowerCase().includes("datetime"))
            {
              tempObj = tempObj + literalIndex;
              literalIndex = literalIndex + 1 ;
              isLitteral = true;
              console.log('tempObj 0 '+tempObj);
            }

            if(!tempDomainArray.length  ==0)
            {
              tempDomainArray.forEach(function(el)
              {
                if(el != "?" && tempPred != "?" && tempObj != "?")
                {
                  if(isLitteral)
                  {
                    var tripleValue = {source:el, 	predicate:tempPred, 	target:tempObj,value: 1};
                    linksArray.push(tripleValue);
                    createNewNode(el,tempPred,tempObj,isLitteral,literalDataType,element);
                  }
                  else
                  {
                    var tripleValue = {source:el, 	predicate:tempPred, 	target:tempObj,value: 1};
                    linksArray.push(tripleValue);
                    createNewNode(el,tempPred,tempObj,isLitteral,literalDataType,element);
                  }
                }
              });
            }
            else
            {
              if(tempSub != "?" && tempPred != "?" && tempObj != "?")
              {
                if(isLitteral)
                {
                  var tripleValue = {source:tempSub, 	predicate:' ', 	target:tempPred,value: 1};
                  linksArray.push(tripleValue);
                  createNewNode(tempSub,'',tempPred,isLitteral,literalDataType,element);
                }
                else
                {
                  var tripleValue = {source:tempSub, 	predicate:tempPred, 	target:tempObj,value: 1};
                  linksArray.push(tripleValue);
                  createNewNode(tempSub,tempPred,tempObj,isLitteral,literalDataType,element);
                }
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
          //.attr("marker-end", "url(#end)")
          .attr("marker-end",function (d) { 
            var labelStr = d.predicate; 
            if(labelStr.includes('subClassOf')){return  "url(#end1)";}
            return  "url(#end)"})
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
                //console.log(d);
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
    
    link.append("title")
              .text(function(d) { var returnValue = 'Source : '+d.source +'\nProperty : '+d.predicate+'\nObject : '+d.target;
                return returnValue;
              });
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
            if(d.isLitteral)
            {return literalRadius;}
            return resourceRadius;})
          .style("fill",function(d){var labelStr = d.id; 
            if(d.isLitteral)
                {return "#FF9800";} 
            return "#311B92";})
          .call(d3.drag()
              .on("start", dragstarted)
              .on("drag", dragged)
             .on("end", dragended)
    );

    var lables = node.append("text")
              .attr("class", "node-text")
              .text(function (d) {return d.id; })
              .style("font-size", function(d){var labelStr = d.id; 
                if(d.isLitteral)
                {return "80%";}
                return "100%";})
              .style("font-style", function(d){var labelStr = d.id; 
                if(d.isLitteral)
                {return "italic";}
                return "bold";})
              .style("fill",function(d){var labelStr = d.id; 
                if(d.isLitteral)
                    {return "#FF9800";}
                return "#311B92";})
              .attr('x', function(d){
                if(d.isLitteral)
                    {return 8;}
                return 12;})
              .attr('y', 3) 
    ;

    node.append("title")
          .text(function(d) { 
            var returnValue = 'Node : '+d.id;
            if(d.isLitteral){returnValue = 'Node : '+d.id +'\nData type : '+d.literalDataType;}
            else{returnValue = 'Node : '+d.id;}
            return returnValue; })
    ;
    
    node.on("dblclick",function(d){   
      var idVal =  "#"+d.nodeId;

      var tempNode = d3.select("#"+idsArray.nodeId)
      tempNode.selectAll("*").remove(); 

      tempNode.append("text")
             .text(d.id) ; 

      
      var nodeLabel = d3.select("#"+idsArray.nodeLabelId)
      nodeLabel.selectAll("*").remove(); 

      console.log('d.nodeContains => '+d.nodeContains);
      console.log('d.nodeContains["rdfs:label"] => '+d.nodeContains["rdfs:label"]);
      
      console.log('d.nodeContains["rdfs:label"]["@value"]  => '+d.nodeContains["rdfs:label"]["@value"]);
      if(d.nodeContains["rdfs:label"] != undefined && d.nodeContains["rdfs:label"]["@value"] != undefined )
      {
        nodeLabel.append("text")
            .text(d.nodeContains["rdfs:label"]["@value"]) ; 
      }
         
      var nodeDescription = d3.select("#"+idsArray.nodeDescripId)
      nodeDescription.selectAll("*").remove(); 
      if(d.nodeContains["dc:description"] != undefined && d.nodeContains["dc:description"]["@value"] != undefined )
      {
        nodeDescription.append("text")
            .text(d.nodeContains["dc:description"]["@value"]) ; 
      }
    });

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
            // return  Math.max(5, Math.min(height - 10, (d.source.y + d.target.y)/2));
            return 4 + (d.source.y + d.target.y)/2 ; })
      ;
      node
        .attr("transform", function(d) {
            return "translate(" + d.x + "," + d.y + ")";
            //return "translate(" + Math.max(10, Math.min(width - 10, d.x)) + "," + Math.max(5, Math.min(height - 10, d.y)) + ")";
            //return "translate(" + (d.x < 1000 ? d.x = 100 : d.x > 1000 ? d.x = 900 : d.x) + "," + (d.y < 1000 ? d.y = 100 : d.y > 900 ? d.y = 900 : d.y) + ")";
          })
      ;
      if(applyNodesBoundary)
      {
        node
        .attr("cx", function(d) { return d.x = Math.max(Math.max(resourceRadius,literalRadius), Math.min(width - Math.max(resourceRadius,literalRadius), d.x)); })
        .attr("cy", function(d) { return d.y = Math.max(Math.max(resourceRadius,literalRadius), Math.min(height - Math.max(resourceRadius,literalRadius), d.y)); });
      }
    }

    if(!disableZoom)
    {
      var zoom_handler = d3.zoom()
        .on("zoom", zoom_actions);
      zoom_handler(svg);
      //Zoom functions 
      function zoom_actions(){
        g.attr("transform", d3.event.transform)
        
      }
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

    function createNewNode(tempSub,tempPred,tempObj,isLitteralVal,literalDataTypeVal,element)
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
        circleIdIndex =circleIdIndex+1; 
        var nodeValue = {nodeContains:element,id:tempSub ,predicate:tempPred,nodeId:"circleId"+circleIdIndex,isLitteral:false,literalDataType:literalDataTypeVal, group: 1};
        nodesArray.push(nodeValue);
      }
      if(objNodeAdded === false)
      {
        circleIdIndex =circleIdIndex+1;
        var nodeValue = {nodeContains:element,id:tempObj ,predicate:tempPred,nodeId:"circleId"+circleIdIndex,isLitteral:isLitteralVal,literalDataType:literalDataTypeVal, group: 1};
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
