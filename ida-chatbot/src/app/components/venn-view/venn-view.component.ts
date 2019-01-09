import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';
declare var d3;

declare function renderVennDiagram(a, b, c, d);
@Component({
  selector: 'app-venn-view',
  templateUrl: './venn-view.component.html',
  styleUrls: ['./venn-view.component.css']
})
export class VennViewComponent implements OnInit, AfterViewInit {
  @Input('data') public demDt;
  public vennContainerId;
  public vennMainId;
  // public demDt = [
  //   {'sets': [0], 'label': '297', 'size': 10000},
  //   {'sets': [1], 'label': '304', 'size': 20000},
  //   {'sets': [2], 'label': '319', 'size': 35000},
  //   // {'sets': [3], 'label': '295', 'size': 27000},
  //   // {'sets': [4], 'label': '204', 'size': 15000},
  //   {'sets': [0, 1], 'size': 500},
  //   {'sets': [0, 2], 'size': 1000},
  //   {'sets': [1, 2], 'size': 2000},
  //   // {'sets': [2, 3], 'size': 2000},
  //   // {'sets': [2, 4], 'size': 3000},
  //   // {'sets': [3, 4], 'size': 3000},
  //   // {'sets': [2, 3, 4], 'size': 500},
  // ];
  public intervalId: any;
  constructor(public uip: UniqueIdProviderService) {
    this.vennContainerId = 'venn-container' + this.uip.getUniqueId();
    this.vennMainId = 'venn-main' + this.uip.getUniqueId();
  }

  ngOnInit() {
    this.demDt = this.demDt;
  }

  ngAfterViewInit() {
    this.intervalId = setInterval(x => {
      this.renderGraph();
    }, 400);

  }

  renderGraph() {
    const svg = d3.select('#' + this.vennContainerId).node();
    if (svg) {
      clearInterval(this.intervalId);
      renderVennDiagram(this.vennContainerId, this.vennMainId, this.demDt.data, this.demDt.label);
    }
  }
}
