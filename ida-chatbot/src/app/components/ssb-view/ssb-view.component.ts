import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';

declare var d3;

declare function renderSsb(a, b);

@Component({
  selector: 'app-ssb-view',
  templateUrl: './ssb-view.component.html',
  styleUrls: ['./ssb-view.component.css']
})
export class SsbViewComponent implements OnInit, AfterViewInit {
  @Input('data') public inputData;
  public ssbIdMap = {};
  public intervalId: any;

  constructor(public uip: UniqueIdProviderService) {
    this.ssbIdMap['mainSsbId'] = 'ssb-main' + this.uip.getUniqueId();
    this.ssbIdMap['sequenceSsbId'] = 'ssb-sequence' + this.uip.getUniqueId();
    this.ssbIdMap['chartSsbId'] = 'ssb-chart' + this.uip.getUniqueId();
    this.ssbIdMap['explanationSsbId'] = 'ssb-explanation' + this.uip.getUniqueId();
    this.ssbIdMap['percentageSsbId'] = 'ssb-percentage' + this.uip.getUniqueId();
    this.ssbIdMap['sidebarSsbId'] = 'ssb-sidebar' + this.uip.getUniqueId();
    this.ssbIdMap['togglelegendSsbId'] = 'ssb-togglelegend' + this.uip.getUniqueId();
    this.ssbIdMap['legendSsbId'] = 'ssb-legend' + this.uip.getUniqueId();
    this.ssbIdMap['containerSsbId'] = 'ssb-container' + this.uip.getUniqueId();
    this.ssbIdMap['trailSsbId'] = 'ssb-trail' + this.uip.getUniqueId();
    this.ssbIdMap['endlabelSsbId'] = 'endlabel' + this.uip.getUniqueId();
  }

  ngOnInit() {
    this.inputData = this.inputData;
  }

  ngAfterViewInit() {
    this.intervalId = setInterval(x => {
      this.renderGraph();
    }, 400);

  }

  renderGraph() {
    const svg = d3.select('#' + this.ssbIdMap['chartSsbId']).node();
    if (svg) {
      clearInterval(this.intervalId);
      renderSsb(this.ssbIdMap, this.inputData);
    }
  }

}
