import { Component, OnInit, AfterViewInit} from '@angular/core';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';

declare function createV4RDFOntologyGraph(a, b,c,d,e,f,g,h,j,i);
@Component({
  selector: 'app-rdf-ontology-view',
  templateUrl: './rdf-ontology-view.component.html',
  styleUrls: ['./rdf-ontology-view.component.css']
})
export class RdfOntologyViewComponent implements OnInit {

  public figId: string;
  public ontologyId: string;
  public fileName: string;
  public nodeId: string;
  public nodeLabelId: string;
  public nodeIdsId: string;
  public nodeTypeId: string;
  public nodeDescripId: string;
  public nodeSubClassId: string;
  public nodeDomainId: string;
  public nodeRangeId: string;
  public nodeCommentId: string;

  languageCheked = false;
  classHeirarchyChecked = true;
  allPropChecked = false;
  allNodesInBoundClicked = false;
  disableZoomClicked = false;
  edgeSize = 50;
  graphCharge = 1000;
  resourceRadius = 10;
  literalRadius = 6;
  resourceNodeColor ="#311B92";
  resourceNodeTextColor = "#311B92";
  literalNodeColor ="#FF9800";
  literalNodeTextColor ="#FF9800";
  subClassLabel ="--subClassOf--";
  subClassLabelColor ="#3498DB";
  propertyTextColor ="#28B463";
  onClickNodeColor ="red";
  
  constructor(public uip: UniqueIdProviderService) {
    this.figId = 'fig' + this.uip.getUniqueId();
    this.ontologyId = 'ontologyId' + this.uip.getUniqueId();
    this.nodeId = 'nodeLabelId' + this.uip.getUniqueId();
    this.nodeLabelId = 'nodeLabelId' + this.uip.getUniqueId();
    this.nodeIdsId = 'nodeIdsId' + this.uip.getUniqueId();
    this.nodeTypeId = 'nodeTypeId' + this.uip.getUniqueId();
    this.nodeDescripId = 'nodeDescripId' + this.uip.getUniqueId();
    this.nodeSubClassId = 'nodeSubClassId' + this.uip.getUniqueId();
    this.nodeDomainId = 'nodeDomainId' + this.uip.getUniqueId();
    this.nodeRangeId = 'nodeRangeId' + this.uip.getUniqueId();
    this.nodeCommentId = 'nodeCommentId' + this.uip.getUniqueId();
  }

  ngOnInit() {
  }

  ngAfterViewInit() 
  {
    this.fileName = "";
    var idVal = {nodeId:this.nodeId,nodeLabelId:this.nodeLabelId,nodeIdsId:this.nodeIdsId,nodeTypeId:this.nodeTypeId,nodeDescripId:this.nodeDescripId,
                nodeSubClassId:this.nodeSubClassId,nodeDomainId:this.nodeDomainId,nodeRangeId:this.nodeRangeId,
                nodeCommentId:this.nodeCommentId
               }
    var customizeGraphVals = {edgeSize:this.edgeSize,
                graphCharge:this.graphCharge,
                resourceRadius:this.resourceRadius,
                literalRadius:this.literalRadius,
                resourceNodeColor:this.resourceNodeColor,
                resourceNodeTextColor:this.resourceNodeTextColor,
                literalNodeColor:this.literalNodeColor,
                literalNodeTextColor:this.literalNodeTextColor,
                subClassLabel:this.subClassLabel,
                subClassLabelColor:this.subClassLabelColor,
                propertyTextColor:this.propertyTextColor,
                onClickNodeColor:this.onClickNodeColor
                }
    createV4RDFOntologyGraph(this.figId, this.ontologyId,this.fileName,this.languageCheked,this.classHeirarchyChecked,
      this.allPropChecked,this.allNodesInBoundClicked,this.disableZoomClicked,idVal,customizeGraphVals);
  }

  checkboxValuesChanged(event: any){
    var idVal = {nodeId:this.nodeId,nodeLabelId:this.nodeLabelId,nodeIdsId:this.nodeIdsId,nodeTypeId:this.nodeTypeId,nodeDescripId:this.nodeDescripId,
                nodeSubClassId:this.nodeSubClassId,nodeDomainId:this.nodeDomainId,nodeRangeId:this.nodeRangeId,
                nodeCommentId:this.nodeCommentId
               }
    var customizeGraphVals = {edgeSize:this.edgeSize,
                graphCharge:this.graphCharge,
                resourceRadius:this.resourceRadius,
                literalRadius:this.literalRadius,
                resourceNodeColor:this.resourceNodeColor,
                resourceNodeTextColor:this.resourceNodeTextColor,
                literalNodeColor:this.literalNodeColor,
                literalNodeTextColor:this.literalNodeTextColor,
                subClassLabel:this.subClassLabel,
                subClassLabelColor:this.subClassLabelColor,
                propertyTextColor:this.propertyTextColor,
                onClickNodeColor:this.onClickNodeColor
                }
    createV4RDFOntologyGraph(this.figId, this.ontologyId,this.fileName,this.languageCheked,this.classHeirarchyChecked,
      this.allPropChecked,this.allNodesInBoundClicked,this.disableZoomClicked,idVal,customizeGraphVals);
  }
  graphResetChanged(event){
    this.languageCheked = false;
    this.classHeirarchyChecked = true;
    this.allPropChecked = false;
    this.allNodesInBoundClicked = false;
    this.disableZoomClicked = false;

    var idVal = {nodeId:this.nodeId,nodeLabelId:this.nodeLabelId,nodeIdsId:this.nodeIdsId,nodeTypeId:this.nodeTypeId,nodeDescripId:this.nodeDescripId,
                nodeSubClassId:this.nodeSubClassId,nodeDomainId:this.nodeDomainId,nodeRangeId:this.nodeRangeId,
                nodeCommentId:this.nodeCommentId
               }

    this.edgeSize = 50;
    this.graphCharge = 1000;
    this.resourceRadius = 10;
    this.literalRadius = 6;
    this.resourceNodeColor ="#311B92";
    this.resourceNodeTextColor = "#311B92";
    this.literalNodeColor ="#FF9800";
    this.literalNodeTextColor ="#FF9800";
    this.subClassLabel ="--subClassOf--";
    this.subClassLabelColor ="#3498DB";
    this.propertyTextColor ="#28B463";
    this.onClickNodeColor ="red";

    var customizeGraphVals = {edgeSize:this.edgeSize,
                              graphCharge:this.graphCharge,
                              resourceRadius:this.resourceRadius,
                              literalRadius:this.literalRadius, 
                              resourceNodeColor:this.resourceNodeColor,
                              resourceNodeTextColor:this.resourceNodeTextColor,
                              literalNodeColor:this.literalNodeColor,
                              literalNodeTextColor:this.literalNodeTextColor,
                              subClassLabel:this.subClassLabel,
                              subClassLabelColor:this.subClassLabelColor,
                              propertyTextColor:this.propertyTextColor,
                              onClickNodeColor:this.onClickNodeColor
                              }

    createV4RDFOntologyGraph(this.figId, this.ontologyId,this.fileName,this.languageCheked,this.classHeirarchyChecked,
      this.allPropChecked,this.allNodesInBoundClicked,this.disableZoomClicked,idVal,customizeGraphVals);
  }

  applyConfigClicked(event){
    
    var idVal = {nodeId:this.nodeId,nodeLabelId:this.nodeLabelId,nodeIdsId:this.nodeIdsId,nodeTypeId:this.nodeTypeId,nodeDescripId:this.nodeDescripId,
                nodeSubClassId:this.nodeSubClassId,nodeDomainId:this.nodeDomainId,nodeRangeId:this.nodeRangeId,
                nodeCommentId:this.nodeCommentId
               }
    var customizeGraphVals = {edgeSize:this.edgeSize,
                graphCharge:this.graphCharge,
                resourceRadius:this.resourceRadius,
                literalRadius:this.literalRadius,
                resourceNodeColor:this.resourceNodeColor,
                resourceNodeTextColor:this.resourceNodeTextColor,
                literalNodeColor:this.literalNodeColor,
                literalNodeTextColor:this.literalNodeTextColor,
                subClassLabel:this.subClassLabel,
                subClassLabelColor:this.subClassLabelColor,
                propertyTextColor:this.propertyTextColor,
                onClickNodeColor:this.onClickNodeColor
                }
    createV4RDFOntologyGraph(this.figId, this.ontologyId,this.fileName,this.languageCheked,this.classHeirarchyChecked,
      this.allPropChecked,this.allNodesInBoundClicked,
      this.disableZoomClicked,idVal,customizeGraphVals);
  }
}