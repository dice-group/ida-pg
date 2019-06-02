//import { Component, OnInit } from '@angular/core';
import {AfterViewChecked, AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';

declare function createV3RDFOntologyView();

@Component({
  selector: 'app-rdf-ontology-view',
  templateUrl: './rdf-ontology-view.component.html',
  styleUrls: ['./rdf-ontology-view.component.css']
})
export class RdfOntologyViewComponent implements OnInit, AfterViewInit {
  //@Input('content')
  public content: any;
  public fdgid: string;
  public svgid: string;
  public graph: any;
  public intervalId: any;

  constructor() {
    
    this.svgid = 'svgid';
  }

  ngOnInit() {
  }
  ngAfterViewInit() {
    createV3RDFOntologyView();
  }
}
