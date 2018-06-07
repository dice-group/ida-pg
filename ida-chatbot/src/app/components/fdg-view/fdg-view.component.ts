import {AfterViewInit, Component, OnInit} from '@angular/core';
@Component({
  selector: 'app-fdg-view',
  templateUrl: './fdg-view.component.html',
  styleUrls: ['./fdg-view.component.css']
})
export class FdgViewComponent implements OnInit, AfterViewInit {

  constructor() { }

  ngOnInit() {
  }

  ngAfterViewInit() {

    var svg = d3.select('#d3_selectable_force_directed_graph');

    d3.json('/assets/fdg/demo.json', function(error, graph) {
      if (!error) {
        //console.log('graph', graph);
        createV4SelectableForceDirectedGraph(svg, graph);
      } else {
        console.error(error);
      }
    });
  }

}
