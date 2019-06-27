function createV4RDFOntologyGraph(figId, svgId, fileName,displayDeustch,displaySubclasses,
                                  dispalyAllprop,applyNodesBoundary,disableZoom,idsArray,customizeGraphArray) {
  var linksArray = [];
  var nodesArray = [];
  var enLabelArray = [];
  var deLabelArray = [];
  var expandedNodesArray = [];
  var nodeContents = [];

  var width = 1060,height = 900,resourceRadius = customizeGraphArray.resourceRadius,literalRadius = customizeGraphArray.literalRadius;

  
  var svgClear = d3.select("#"+svgId);
  svgClear.selectAll("*").remove(); 

  var svg = d3.select("#"+svgId)
    .append("svg")
    .attr("width", width)
    .attr("height", height);
 
  svg.append("svg:defs").selectAll("marker")
  .data(["normal"])
  .enter().append("svg:marker")
  .attr("id", String)
  .attr("viewBox", "0 -4 10 10")
  .attr("refX", 30) 
  .attr("refY", -0.5)
  .attr("markerWidth", 26)
  .attr("markerHeight", 6)
  .attr("orient", "auto")
  .append("svg:polyline")
  .style("fill","black")
  .attr("points", "0,-4 20,0 0,5")
  ;
  svg.append("svg:defs").selectAll("marker")
  .data(["subclass"])
  .enter().append("svg:marker")
  .attr("id", String)
  .attr("viewBox", "0 -4 10 10")
  .attr("refX", 30) 
  .attr("refY", -0.5)
  .attr("markerWidth", 26)
  .attr("markerHeight", 6)
  .attr("orient", "auto")
  .append("svg:polyline")
  .style("fill", "grey")
  .attr("points", "0,-4 20,0 0,5")
  ;
  

  var color = d3.scaleOrdinal(d3.schemeCategory20);

  var simulation = d3.forceSimulation()
    .force("link", d3.forceLink().id(function(d) { return d.id; }).distance(customizeGraphArray.edgeSize))
    .force("charge", d3.forceManyBody().strength(-customizeGraphArray.graphCharge)) //Charge strength is here
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

              var tepmNodeContent = {label:eachIds["@value"], contents:elem};
              nodeContents.push(tepmNodeContent);
            }
            if(eachIds["@language"] == "en")
            {
              var tempLabel = {"id":elem["@id"],"language":"en","value":eachIds["@value"]}
              enLabelArray.push(tempLabel);

              var tepmNodeContent = {label:eachIds["@value"], contents:elem};
              nodeContents.push(tepmNodeContent);
            }
           
            var tempLabelArray = elem["@id"].split('/');
            var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
            var tempLabel = tempLabelArray1[tempLabelArray1.length -1 ]+'?';

            var tepmNodeContent = {label:tempLabel, contents:elem};
            nodeContents.push(tepmNodeContent);
            

          });
        }
        else
        {
          if(elem["rdfs:label"]["@language"] == "de")
          {
            var tempLabel = {"id":elem["@id"],"language":"de","value":elem["rdfs:label"]["@value"]}
            deLabelArray.push(tempLabel);

            var tepmNodeContent = {label:elem["rdfs:label"]["@value"], contents:elem};
            nodeContents.push(tepmNodeContent);
          }
          if(elem["rdfs:label"]["@language"] == "en")
          {
            var tempLabel = {"id":elem["@id"],"language":"en","value":elem["rdfs:label"]["@value"]}
            enLabelArray.push(tempLabel);

            var tepmNodeContent = {label:elem["rdfs:label"]["@value"], contents:elem};
            nodeContents.push(tepmNodeContent);
          }
          
          var tempLabelArray = elem["@id"].split('/');
          var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
          var tempLabel = tempLabelArray1[tempLabelArray1.length -1 ]+'?';

          var tepmNodeContent = {label:tempLabel, contents:elem};
          nodeContents.push(tepmNodeContent);
         }
      }
      else
      {
        var tempLabelArray = elem["@id"].split('/');
        var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
        var tempLabel = tempLabelArray1[tempLabelArray1.length -1 ]+'?';

        var tepmNodeContent = {label:tempLabel, contents:elem};
        nodeContents.push(tepmNodeContent);
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
                  createNewNode(tempSub,pred,tempObj,false,'');
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
                createNewNode(tempSub,pred,tempObj,false,'');
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
                    createNewNode(el,tempPred,tempObj,isLitteral,literalDataType);
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
                  createNewNode(tempSub,'',tempPred,isLitteral,literalDataType);
                }
                else
                {
                  var tripleValue = {source:tempSub, 	predicate:tempPred, 	target:tempObj,value: 1};
                  linksArray.push(tripleValue);
                  createNewNode(tempSub,tempPred,tempObj,isLitteral,literalDataType);
                }
              }
            }
        }
        //END :: Nodes and Links creation for all Properties
      });
      //END :: Creation of Graph depenging on displaySubclasses and dispalyAllprop values

      if (error) {
          return console.warn('Error in createV3RDFOntologyGraphView() method :'+error);
      }
      
      var g = svg.append("g")
          .attr("class", "everything");
          
        

    var link =//g.attr("class", "links")
          g.selectAll("line")
          .data(linksArray)
          .enter().append("line")
          .attr("class", "link")
          .attr("stroke-width",1)
          .style("fill","#999")
          //.attr("marker-end", "url(#end)")
          .attr("marker-end",function (d) { 
            var labelStr = d.predicate; 
            if(labelStr.includes('subClassOf')){return  "url(#subclass)";}
            return  "url(#normal)"})
          .style("stroke-dasharray",function (d) { 
            var labelStr = d.predicate; 
            if(labelStr.includes('subClassOf')){return  ("3, 3");}
            if(labelStr.includes('subPropertyOf')){return  ("5, 5");}
            return  ("0, 0");})
          
          ;
          /*
          link.append("title")
          .text(function(d) { var returnValue = 'Source : '+d.source +'\nProperty : '+d.predicate+'\nObject : '+d.target;
         
          return returnValue;
            
          });*/
;


          // ==================== Add Link Names =====================
    var linkTexts = g.selectAll(".link-text")
            .data(linksArray)
            .enter()
            .append("text")
              .attr("class", "link-text")
              .text( function (d) { 
                var labelStr = d.predicate; 
                if(labelStr.includes('subClassOf'))
                  {return customizeGraphArray.subClassLabel;}
                return d.predicate; }) 
                .style("fill",function(d){var labelStr = d.predicate; 
                  if(labelStr.includes('subClassOf'))
                  {return customizeGraphArray.subClassLabelColor;}
                  return customizeGraphArray.propertyTextColor;})
                .style("font-size", "80%")
                .style("font-style", "italic")
                .attr('x', 12)
                .attr('y', 3)
    ;    
    /*
    link.append("title")
              .text(function(d) { var returnValue = 'Source : '+d.source +'\nProperty : '+d.predicate+'\nObject : '+d.target;
                return returnValue;
              });
    ; */

    linkTexts.append("title")
              .text(function(d) { 
                var labelStr = d.predicate; 
                if(labelStr.includes('subClassOf'))
                {
                    var returnValue = 'Source : '+d.source +'\nProperty : '+customizeGraphArray.subClassLabel+'\nObject : '+d.target;
                }
                else
                {
                  var returnValue = 'Source : '+d.source +'\nProperty : '+d.predicate+'\nObject : '+d.target;
                }
                
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
                {return customizeGraphArray.literalNodeColor;} 
            return customizeGraphArray.resourceNodeColor;})
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
                    {return customizeGraphArray.literalNodeTextColor;}
                return customizeGraphArray.resourceNodeTextColor;})
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
    
    node.on("dblclick",function(d)
    {   
      var nodeIdVal =  d.id;
      var tempNodeContains = [];
      nodeContents.forEach(function(val){
        if(val.label == nodeIdVal)
        {
          tempNodeContains = val.contents
        }
      });

      var tempNode = d3.select("#"+idsArray.nodeId)
      tempNode.selectAll("*").remove(); 
      tempNode.append("text")
             .text(" " + nodeIdVal+"\n")
              .style("font-style","italic")
              .style("font-size", "80%");

    
      //node label on double click
      if(tempNodeContains != undefined)
      {
        var nodeLabel = d3.select("#"+idsArray.nodeLabelId)
        nodeLabel.selectAll("*").remove();
        if(tempNodeContains["rdfs:label"] != undefined)
        { 
          if(Object.prototype.toString.call(tempNodeContains["rdfs:label"]) === '[object Array]')
          {
            tempNodeContains["rdfs:label"].forEach(function(eachIds)
            {
              if(eachIds["@language"] == "de" && displayDeustch)
              {
                
                nodeLabel.append("text")
                  .text(" " + eachIds["@value"] +"\n")
                    .style("font-style","italic")
                    .style("font-size", "80%");
              }
              else if (eachIds["@language"] == "en" && !displayDeustch)
              {
                nodeLabel.append("text")
                  .text(" " + eachIds["@value"] +"\n")
                    .style("font-style","italic")
                    .style("font-size", "80%");
              }
  
            });
          }
          else
          {
            nodeLabel.append("text")
              .text(" " + tempNodeContains["rdfs:label"]["@value"] +"\n")
                .style("font-style","italic")
                .style("font-size", "80%");
          }
        }
  
        //node id on double click
        var nodeID = d3.select("#"+idsArray.nodeIdsId)
        nodeID.selectAll("*").remove(); 
        if(tempNodeContains["@id"] != undefined )
        {
          nodeID.append("text")
              .text(" " + tempNodeContains["@id"]+"\n") 
                .style("font-style","italic")
                .style("font-size", "80%");
        }
        //node type on double click
        var nodeType = d3.select("#"+idsArray.nodeTypeId)
        nodeType.selectAll("*").remove(); 
        if(tempNodeContains["@type"] != undefined)
        {
          nodeType.append("text")
              .text(" " + tempNodeContains["@type"]+"\n\n")
                .style("font-style","italic")
                .style("font-size", "80%"); 
        }
        
        //node description on double click
        var nodeDescription = d3.select("#"+idsArray.nodeDescripId)
        nodeDescription.selectAll("*").remove();
        if(tempNodeContains["dc:description"] != undefined)
        { 
          if(Object.prototype.toString.call(tempNodeContains["dc:description"]) === '[object Array]')
          {
            tempNodeContains["dc:description"].forEach(function(eachIds)
            {
              if(eachIds["@language"] == "de" && displayDeustch)
              {
                
                nodeDescription.append("text")
                  .text(" " + eachIds["@value"] +"\n")
                    .style("font-style","italic")
                    .style("font-size", "80%");
              }
              else if (eachIds["@language"] == "en" && !displayDeustch)
              {
                nodeDescription.append("text")
                  .text(" " + eachIds["@value"] +"\n")
                    .style("font-style","italic")
                    .style("font-size", "80%");
              }

            });
          }
          else
          {
            nodeDescription.append("text")
              .text(" " + tempNodeContains["dc:description"]["@value"] +"\n")
                .style("font-style","italic")
                .style("font-size", "80%");
          }
        }
        
        var nodeSubClass = d3.select("#"+idsArray.nodeSubClassId)
        nodeSubClass.selectAll("*").remove(); 
        if(tempNodeContains["rdfs:subClassOf"] != undefined)
        {
          if(Object.prototype.toString.call(tempNodeContains["rdfs:subClassOf"]) === '[object Array]')
          {
            var tempIndexSubClassof =1;
            tempNodeContains["rdfs:subClassOf"].forEach(function(eachIds)
            {
              var subClassLabel = getLabel(displayDeustch,eachIds["@id"]);
              if(subClassLabel == "noLabel")
              {
                var tempLabelArray = eachIds["@id"].split('/');
                var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
                subClassLabel = tempLabelArray1[tempLabelArray1.length -1 ]+'?';
              }

              nodeSubClass.append("text")
                  .text(" " + tempIndexSubClassof +". "+ subClassLabel +"\n")
                    .style("font-style","italic")
                    .style("font-size", "80%");
              
              tempIndexSubClassof = tempIndexSubClassof+1;

            });
          }
          else
          {
            var subClassLabel = getLabel(displayDeustch,tempNodeContains["rdfs:subClassOf"]["@id"]);
            if(subClassLabel == "noLabel")
            {
              var tempLabelArray = tempNodeContains["rdfs:subClassOf"]["@id"].split('/');
              var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
              subClassLabel = tempLabelArray1[tempLabelArray1.length -1 ]+'?';
            }

            nodeSubClass.append("text")
              .text( " " + subClassLabel+"\n")
                .style("font-style","italic")
                .style("font-size", "80%");
          }
        }

        var nodeDomain = d3.select("#"+idsArray.nodeDomainId)
        nodeDomain.selectAll("*").remove(); 
        if(tempNodeContains["rdfs:domain"] != undefined )
        {
          
          if(Object.prototype.toString.call(tempNodeContains["rdfs:domain"]) === '[object Array]')
          {
            var tempIndexdomain =1;
            tempNodeContains["rdfs:domain"].forEach(function(eachIds)
            {
              var domainLabel = getLabel(displayDeustch,eachIds["@id"]);
              if(domainLabel == "noLabel")
              {
                var tempLabelArray = eachIds["@id"].split('/');
                var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
                domainLabel = tempLabelArray1[tempLabelArray1.length -1 ]+'?';
              }

              nodeDomain.append("text")
                  .text(" " +  tempIndexdomain +". "+ domainLabel +"\n")
                    .style("font-style","italic")
                    .style("font-size", "80%");
              
              tempIndexdomain = tempIndexdomain+1;

            });
          }
          else
          {
            var domainLabel = getLabel(displayDeustch,tempNodeContains["rdfs:domain"]["@id"]);
            if(domainLabel == "noLabel")
            {
              var tempLabelArray = tempNodeContains["rdfs:domain"]["@id"].split('/');
              var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
              domainLabel = tempLabelArray1[tempLabelArray1.length -1 ]+'?';
            }

            nodeDomain.append("text")
              .text(" " + domainLabel+"\n")
                .style("font-style","italic")
                .style("font-size", "80%");
          }  
        }


        var nodeRange = d3.select("#"+idsArray.nodeRangeId)
        nodeRange.selectAll("*").remove(); 
        if(tempNodeContains["rdfs:range"] != undefined )
        {
          
          if(Object.prototype.toString.call(tempNodeContains["rdfs:range"]) === '[object Array]')
          {
            var tempIndexRange =1;
            tempNodeContains["rdfs:range"].forEach(function(eachIds)
            {
              var rangeLabel = getLabel(displayDeustch,eachIds["@id"]);
              if(rangeLabel == "noLabel")
              {
                var tempLabelArray = eachIds["@id"].split('/');
                var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
                rangeLabel = tempLabelArray1[tempLabelArray1.length -1 ]+'?';
              }

              nodeRange.append("text")
                  .text(" " +  tempIndexRange +". "+ rangeLabel +"\n")
                    .style("font-style","italic")
                    .style("font-size", "80%");
              
              tempIndexRange = tempIndexRange+1;

            });
          }
          else
          {
            var rangeLabel = getLabel(displayDeustch,tempNodeContains["rdfs:range"]["@id"]);
            if(rangeLabel == "noLabel")
            {
              var tempLabelArray = tempNodeContains["rdfs:range"]["@id"].split('/');
              var tempLabelArray1 = tempLabelArray[tempLabelArray.length -1 ].split(':');
              rangeLabel = tempLabelArray1[tempLabelArray1.length -1 ]+'?';
            }

            nodeRange.append("text")
              .text(" " + rangeLabel+"\n")
                .style("font-style","italic")
                .style("font-size", "80%");
          }  
        }
        
        var nodeComment = d3.select("#"+idsArray.nodeCommentId)
        nodeComment.selectAll("*").remove(); 
        if(tempNodeContains["rdfs:comment"] != undefined)
        {
          nodeComment.append("text")
              .text(tempNodeContains["rdfs:comment"]+"\n") 
                .style("font-style","italic")
                .style("font-size", "80%");
        }

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
      //zoom_handler(svg);
      //Zoom functions 
      svg.call(zoom_handler).on("dblclick.zoom", null);
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

    function createNewNode(tempSub,tempPred,tempObj,isLitteralVal,literalDataTypeVal)
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
        var nodeValue = {id:tempSub ,predicate:tempPred,nodeId:"circleId"+circleIdIndex,isLitteral:false,literalDataType:literalDataTypeVal, group: 1};
        nodesArray.push(nodeValue);
      }
      if(objNodeAdded === false)
      {
        circleIdIndex =circleIdIndex+1;
        var nodeValue = {id:tempObj ,predicate:tempPred,nodeId:"circleId"+circleIdIndex,isLitteral:isLitteralVal,literalDataType:literalDataTypeVal, group: 1};
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
