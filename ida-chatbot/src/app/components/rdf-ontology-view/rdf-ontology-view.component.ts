import { Component, OnInit, AfterViewInit} from '@angular/core';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';

declare function createV4RDFOntologyGraph(a, b,c,d,e,f,g,h,j);
@Component({
  selector: 'app-rdf-ontology-view',
  templateUrl: './rdf-ontology-view.component.html',
  styleUrls: ['./rdf-ontology-view.component.css']
})
export class RdfOntologyViewComponent implements OnInit {

  public figId: string;
  public svgId: string;
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
  edgeSize = 100;
  graphCharge = 1000;
  resourceRadius = 10;
  literalRadius = 6;
  resourceNodeColor ="#311B92";
  
  constructor(public uip: UniqueIdProviderService) {
    this.figId = 'fig' + this.uip.getUniqueId();
    this.svgId = 'svg' + this.uip.getUniqueId();
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
    createV4RDFOntologyGraph(this.figId, this.svgId,this.fileName,this.languageCheked,this.classHeirarchyChecked,
      this.allPropChecked,this.allNodesInBoundClicked,this.disableZoomClicked,idVal);
  }

  checkboxValuesChanged(event: any){
    var idVal = {nodeId:this.nodeId,nodeLabelId:this.nodeLabelId,nodeIdsId:this.nodeIdsId,nodeTypeId:this.nodeTypeId,nodeDescripId:this.nodeDescripId,
                nodeSubClassId:this.nodeSubClassId,nodeDomainId:this.nodeDomainId,nodeRangeId:this.nodeRangeId,
                nodeCommentId:this.nodeCommentId
               }
    createV4RDFOntologyGraph(this.figId, this.svgId,this.fileName,this.languageCheked,this.classHeirarchyChecked,
      this.allPropChecked,this.allNodesInBoundClicked,this.disableZoomClicked,idVal);
    
    /*if(event === "yes")
    {
      createV4RDFOntologyGraph(this.figId1, this.svgId1,this.fileName,"de",true,true);
    }
    else if(event === "no")
    {
      createV4RDFOntologyGraph(this.figId1, this.svgId1,this.fileName,"en",true,false);
    }*/
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
    createV4RDFOntologyGraph(this.figId, this.svgId,this.fileName,this.languageCheked,this.classHeirarchyChecked,
      this.allPropChecked,this.allNodesInBoundClicked,this.disableZoomClicked,idVal);
  }

  applyConfigClicked(event){
    this.languageCheked = false;
    this.classHeirarchyChecked = true;
    this.allPropChecked = false;
    this.allNodesInBoundClicked = false;
    this.disableZoomClicked = false;
    
    var idVal = {nodeId:this.nodeId,nodeLabelId:this.nodeLabelId,nodeIdsId:this.nodeIdsId,nodeTypeId:this.nodeTypeId,nodeDescripId:this.nodeDescripId,
                nodeSubClassId:this.nodeSubClassId,nodeDomainId:this.nodeDomainId,nodeRangeId:this.nodeRangeId,
                nodeCommentId:this.nodeCommentId
               }
    createV4RDFOntologyGraph(this.figId, this.svgId,this.fileName,this.languageCheked,this.classHeirarchyChecked,
      this.allPropChecked,this.allNodesInBoundClicked,
      this.disableZoomClicked,idVal);
  }
}
