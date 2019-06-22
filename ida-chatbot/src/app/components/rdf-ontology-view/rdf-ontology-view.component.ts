import { Component, OnInit,AfterViewInit,} from '@angular/core';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';

declare function createV4RDFOntologyGraph(a, b,c);
@Component({
  selector: 'app-rdf-ontology-view',
  templateUrl: './rdf-ontology-view.component.html',
  styleUrls: ['./rdf-ontology-view.component.css']
})
export class RdfOntologyViewComponent implements OnInit {

  public figId: string;
  public svgId: string;
  public fileName: string;

  constructor(public uip: UniqueIdProviderService) {
    this.figId = 'fig' + this.uip.getUniqueId();
    this.svgId = 'svg' + this.uip.getUniqueId();
  }

  ngOnInit() {
  }
  ngAfterViewInit() {

    //const svg = d3.select('#' + this.fdgid);
    this.fileName = "";
    console.log("before calling ");
    //createV4RDFOntologyClasshierarchy(this.figId, this.svgId,this.fileName);
    createV4RDFOntologyGraph(this.figId, this.svgId,this.fileName);
    console.log("After calling ");
    //createV4RDFOntologyGraphtest(this.figId2, this.svgId2,this.fileName);
  }
}
