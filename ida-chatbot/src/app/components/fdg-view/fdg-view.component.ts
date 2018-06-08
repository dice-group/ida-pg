import {AfterViewChecked, AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {Message} from '../../models/message';

declare var d3;
declare function createV4SelectableForceDirectedGraph(a, b);

@Component({
  selector: 'app-fdg-view',
  templateUrl: './fdg-view.component.html',
  styleUrls: ['./fdg-view.component.css']
})
export class FdgViewComponent implements OnInit, AfterViewInit {
  @Input('idpf')
  public idpf: number;

  public fdgid: string;

  constructor() {
  }

  ngOnInit() {
    this.fdgid = 'fdg' + this.idpf;
  }

  ngAfterViewInit() {

    const svg = d3.select('#' + this.fdgid);
    d3.json('/assets/fdg/demo.json', function (error, graph) {
      if (!error) {
        createV4SelectableForceDirectedGraph(svg, graph);
      } else {
        console.error(error);
      }
    });
  }

}
