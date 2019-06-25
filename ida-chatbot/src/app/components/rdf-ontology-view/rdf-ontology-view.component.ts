import { Component, OnInit,AfterViewInit,} from '@angular/core';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';

declare function createV4RDFOntologyGraph(a, b,c,d,e,f,g,h);
@Component({
  selector: 'app-rdf-ontology-view',
  templateUrl: './rdf-ontology-view.component.html',
  styleUrls: ['./rdf-ontology-view.component.css']
})
export class RdfOntologyViewComponent implements OnInit {

  public figId: string;
  public svgId: string;
  public fileName: string;
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
  }

  ngOnInit() {
  }
  ngAfterViewInit() {
    this.fileName = "";
    createV4RDFOntologyGraph(this.figId, this.svgId,this.fileName,this.languageCheked,this.classHeirarchyChecked,this.allPropChecked,this.allNodesInBoundClicked,this.disableZoomClicked);
  }

  checkboxValuesChanged(event: any){
    createV4RDFOntologyGraph(this.figId, this.svgId,this.fileName,this.languageCheked,this.classHeirarchyChecked,this.allPropChecked,this.allNodesInBoundClicked,this.disableZoomClicked);
    
  }
  graphResetChanged(event){
    this.languageCheked = false;
    this.classHeirarchyChecked = true;
    this.allPropChecked = false;
    this.allNodesInBoundClicked = false;
    this.disableZoomClicked = false;
    createV4RDFOntologyGraph(this.figId, this.svgId,this.fileName,this.languageCheked,this.classHeirarchyChecked,this.allPropChecked,this.allNodesInBoundClicked,this.disableZoomClicked);
  }

  applyConfigClicked(event){
    this.languageCheked = false;
    this.classHeirarchyChecked = true;
    this.allPropChecked = false;
    this.allNodesInBoundClicked = false;
    this.disableZoomClicked = false;
    
    createV4RDFOntologyGraph(this.figId, this.svgId,this.fileName,this.languageCheked,this.classHeirarchyChecked,this.allPropChecked,this.allNodesInBoundClicked,this.disableZoomClicked);
  }
}
