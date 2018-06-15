import {AfterViewChecked, AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';

declare var d3;

declare function createV4SelectableForceDirectedGraph(a, b);

@Component({
  selector: 'app-fdg-view',
  templateUrl: './fdg-view.component.html',
  styleUrls: ['./fdg-view.component.css']
})
export class FdgViewComponent implements OnInit, AfterViewInit {

  @Input('content')
  public content: any;
  public fdgid: string;
  public graph: any;
  public intervalId: any;

  constructor(public uip: UniqueIdProviderService) {
    this.fdgid = 'fdg' + this.uip.getUniqueId();
  }

  ngOnInit() {
    this.graph = this.content;
  }

  ngAfterViewInit() {

    const svg = d3.select('#' + this.fdgid);
    /*d3.json('/assets/fdg/demo.json', function (error, graph) {
      if (!error) {
        createV4SelectableForceDirectedGraph(svg, graph);
      } else {
        console.error(error);
      }
    });*/

    this.intervalId = setInterval(x => {
      this.renderGraph();
    }, 400);

  }

  renderGraph() {
    const svg = d3.select('#' + this.fdgid);
    if (svg) {
      clearInterval(this.intervalId);
      createV4SelectableForceDirectedGraph(svg, this.graph);
    }
  }

}
