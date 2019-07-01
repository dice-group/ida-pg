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
  public intervalId: any;
  public showSpinner: boolean;
  constructor(public uip: UniqueIdProviderService) {
    this.vennContainerId = 'venn-container' + this.uip.getUniqueId();
    this.vennMainId = 'venn-main' + this.uip.getUniqueId();
  }

  ngOnInit() {
    this.demDt = this.demDt;
    this.showSpinner = !!this.demDt.timeInterval;
  }

  ngAfterViewInit() {
    this.intervalId = setInterval(x => {
      this.showSpinner = false;
      this.renderGraph();
    }, this.demDt.timeInterval ? this.demDt.timeInterval : 400);

  }

  renderGraph() {
    const svg = d3.select('#' + this.vennContainerId).node();
    if (svg) {
      clearInterval(this.intervalId);
      renderVennDiagram(this.vennContainerId, this.vennMainId, this.demDt.data, this.demDt.label);
    }
  }
}
