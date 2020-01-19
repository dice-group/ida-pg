import { Component, OnInit, AfterViewInit, Input, AfterContentInit } from '@angular/core';
import { UniqueIdProviderService } from '../../service/misc/unique-id-provider.service';
import { IdaEventService } from 'src/app/service/event/ida-event.service';

declare function createV4RDFOntologyGraph(a, b, c, d, e, f, g, h, j, i);
@Component({
  selector: 'app-rdf-ontology-view',
  templateUrl: './rdf-ontology-view.component.html',
  styleUrls: ['./rdf-ontology-view.component.css']
})
export class RdfOntologyViewComponent implements OnInit, AfterViewInit, AfterContentInit {

  @Input('data') inputData: any;

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
  ontologyData: any = {};
  title = "";


  expandNodeInfoCard = false;
  languageCheked = false;
  classHeirarchyChecked = true;
  allPropChecked = false;
  allNodesInBoundClicked = false;
  disableZoomClicked = false;
  edgeSize = 50;
  graphCharge = 1000;
  resourceRadius = 10;
  literalRadius = 6;
  resourceNodeColor = "#311B92";
  resourceNodeTextColor = "#311B92";
  literalNodeColor = "#FF9800";
  literalNodeTextColor = "#FF9800";
  subClassLabel = "--subClassOf--";
  subClassLabelColor = "#3498DB";
  propertyTextColor = "#28B463";
  onClickNodeColor = "red";

  constructor(
    private uip: UniqueIdProviderService,
    private eventService: IdaEventService
  ) {
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
    this.ontologyData = this.inputData.ontologyData || {};
    this.title = this.inputData.actvDs || "";
  }

  ngAfterContentInit() {
    setTimeout(()=>{
      this.loadGraph();
    }, 500);  
  }

  loadGraph()
  {
    this.fileName = "";
    var idVal = {
      nodeId: this.nodeId, nodeLabelId: this.nodeLabelId, nodeIdsId: this.nodeIdsId, nodeTypeId: this.nodeTypeId, nodeDescripId: this.nodeDescripId,
      nodeSubClassId: this.nodeSubClassId, nodeDomainId: this.nodeDomainId, nodeRangeId: this.nodeRangeId,
      nodeCommentId: this.nodeCommentId
    }
    var customizeGraphVals = {
      edgeSize: this.edgeSize,
      graphCharge: this.graphCharge,
      resourceRadius: this.resourceRadius,
      literalRadius: this.literalRadius,
      resourceNodeColor: this.resourceNodeColor,
      resourceNodeTextColor: this.resourceNodeTextColor,
      literalNodeColor: this.literalNodeColor,
      literalNodeTextColor: this.literalNodeTextColor,
      subClassLabel: this.subClassLabel,
      subClassLabelColor: this.subClassLabelColor,
      propertyTextColor: this.propertyTextColor,
      onClickNodeColor: this.onClickNodeColor
    }
    createV4RDFOntologyGraph(this.figId, this.ontologyId, this.ontologyData, this.languageCheked, this.classHeirarchyChecked,
      this.allPropChecked, this.allNodesInBoundClicked, this.disableZoomClicked, idVal, customizeGraphVals);
  }
  ngAfterViewInit() {
    this.fileName = "";
    var idVal = {
      nodeId: this.nodeId, nodeLabelId: this.nodeLabelId, nodeIdsId: this.nodeIdsId, nodeTypeId: this.nodeTypeId, nodeDescripId: this.nodeDescripId,
      nodeSubClassId: this.nodeSubClassId, nodeDomainId: this.nodeDomainId, nodeRangeId: this.nodeRangeId,
      nodeCommentId: this.nodeCommentId
    }
    var customizeGraphVals = {
      edgeSize: this.edgeSize,
      graphCharge: this.graphCharge,
      resourceRadius: this.resourceRadius,
      literalRadius: this.literalRadius,
      resourceNodeColor: this.resourceNodeColor,
      resourceNodeTextColor: this.resourceNodeTextColor,
      literalNodeColor: this.literalNodeColor,
      literalNodeTextColor: this.literalNodeTextColor,
      subClassLabel: this.subClassLabel,
      subClassLabelColor: this.subClassLabelColor,
      propertyTextColor: this.propertyTextColor,
      onClickNodeColor: this.onClickNodeColor
    }
    createV4RDFOntologyGraph(this.figId, this.ontologyId, this.ontologyData, this.languageCheked, this.classHeirarchyChecked,
      this.allPropChecked, this.allNodesInBoundClicked, this.disableZoomClicked, idVal, customizeGraphVals);
  }

  checkboxValuesChanged(event: any) {
    var idVal = {
      nodeId: this.nodeId, nodeLabelId: this.nodeLabelId, nodeIdsId: this.nodeIdsId, nodeTypeId: this.nodeTypeId, nodeDescripId: this.nodeDescripId,
      nodeSubClassId: this.nodeSubClassId, nodeDomainId: this.nodeDomainId, nodeRangeId: this.nodeRangeId,
      nodeCommentId: this.nodeCommentId
    }
    var customizeGraphVals = {
      edgeSize: this.edgeSize,
      graphCharge: this.graphCharge,
      resourceRadius: this.resourceRadius,
      literalRadius: this.literalRadius,
      resourceNodeColor: this.resourceNodeColor,
      resourceNodeTextColor: this.resourceNodeTextColor,
      literalNodeColor: this.literalNodeColor,
      literalNodeTextColor: this.literalNodeTextColor,
      subClassLabel: this.subClassLabel,
      subClassLabelColor: this.subClassLabelColor,
      propertyTextColor: this.propertyTextColor,
      onClickNodeColor: this.onClickNodeColor
    }
    createV4RDFOntologyGraph(this.figId, this.ontologyId, this.ontologyData, this.languageCheked, this.classHeirarchyChecked,
      this.allPropChecked, this.allNodesInBoundClicked, this.disableZoomClicked, idVal, customizeGraphVals);
  }
  graphResetChanged(event) {
    this.languageCheked = false;
    this.classHeirarchyChecked = true;
    this.allPropChecked = false;
    this.allNodesInBoundClicked = false;
    this.disableZoomClicked = false;

    var idVal = {
      nodeId: this.nodeId, nodeLabelId: this.nodeLabelId, nodeIdsId: this.nodeIdsId, nodeTypeId: this.nodeTypeId, nodeDescripId: this.nodeDescripId,
      nodeSubClassId: this.nodeSubClassId, nodeDomainId: this.nodeDomainId, nodeRangeId: this.nodeRangeId,
      nodeCommentId: this.nodeCommentId
    }

    this.edgeSize = 50;
    this.graphCharge = 1000;
    this.resourceRadius = 10;
    this.literalRadius = 6;
    this.resourceNodeColor = "#311B92";
    this.resourceNodeTextColor = "#311B92";
    this.literalNodeColor = "#FF9800";
    this.literalNodeTextColor = "#FF9800";
    this.subClassLabel = "--subClassOf--";
    this.subClassLabelColor = "#3498DB";
    this.propertyTextColor = "#28B463";
    this.onClickNodeColor = "red";

    var customizeGraphVals = {
      edgeSize: this.edgeSize,
      graphCharge: this.graphCharge,
      resourceRadius: this.resourceRadius,
      literalRadius: this.literalRadius,
      resourceNodeColor: this.resourceNodeColor,
      resourceNodeTextColor: this.resourceNodeTextColor,
      literalNodeColor: this.literalNodeColor,
      literalNodeTextColor: this.literalNodeTextColor,
      subClassLabel: this.subClassLabel,
      subClassLabelColor: this.subClassLabelColor,
      propertyTextColor: this.propertyTextColor,
      onClickNodeColor: this.onClickNodeColor
    }

    createV4RDFOntologyGraph(this.figId, this.ontologyId, this.ontologyData, this.languageCheked, this.classHeirarchyChecked,
      this.allPropChecked, this.allNodesInBoundClicked, this.disableZoomClicked, idVal, customizeGraphVals);
  }

  applyConfigClicked(event) {

    var idVal = {
      nodeId: this.nodeId, nodeLabelId: this.nodeLabelId, nodeIdsId: this.nodeIdsId, nodeTypeId: this.nodeTypeId, nodeDescripId: this.nodeDescripId,
      nodeSubClassId: this.nodeSubClassId, nodeDomainId: this.nodeDomainId, nodeRangeId: this.nodeRangeId,
      nodeCommentId: this.nodeCommentId
    }
    var customizeGraphVals = {
      edgeSize: this.edgeSize,
      graphCharge: this.graphCharge,
      resourceRadius: this.resourceRadius,
      literalRadius: this.literalRadius,
      resourceNodeColor: this.resourceNodeColor,
      resourceNodeTextColor: this.resourceNodeTextColor,
      literalNodeColor: this.literalNodeColor,
      literalNodeTextColor: this.literalNodeTextColor,
      subClassLabel: this.subClassLabel,
      subClassLabelColor: this.subClassLabelColor,
      propertyTextColor: this.propertyTextColor,
      onClickNodeColor: this.onClickNodeColor
    }
    createV4RDFOntologyGraph(this.figId, this.ontologyId, this.ontologyData, this.languageCheked, this.classHeirarchyChecked,
      this.allPropChecked, this.allNodesInBoundClicked,
      this.disableZoomClicked, idVal, customizeGraphVals);
  }
}