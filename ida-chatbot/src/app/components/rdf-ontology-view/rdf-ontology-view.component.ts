import { Component, OnInit,AfterViewInit,} from '@angular/core';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';

declare function createV4RDFOntologyGraph(a, b,c,d,e,f);
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
  
  constructor(public uip: UniqueIdProviderService) {
    this.figId = 'fig' + this.uip.getUniqueId();
    this.svgId = 'svg' + this.uip.getUniqueId();
  }

  ngOnInit() {
  }
  ngAfterViewInit() {
    this.fileName = "";
    createV4RDFOntologyGraph(this.figId, this.svgId,this.fileName,this.languageCheked,this.classHeirarchyChecked,this.allPropChecked);  
  }

  checkboxValuesChanged(event: any){
    createV4RDFOntologyGraph(this.figId, this.svgId,this.fileName,this.languageCheked,this.classHeirarchyChecked,this.allPropChecked);  
  }
}
