import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';

declare var d3;

declare function generateBarGraph(a, b);

@Component({
  selector: 'app-bargraph-view',
  templateUrl: './bargraph-view.component.html',
  styleUrls: ['./bargraph-view.component.css']
})
export class BargraphViewComponent implements OnInit, AfterViewInit {
  @Input('content')
  public content: any;
  public bgid: string;
  public intervalId: any;
  public showSpinner: boolean;
  constructor(public uip: UniqueIdProviderService) {
    this.bgid = 'bg' + this.uip.getUniqueId();
  }

  ngOnInit() {
    this.showSpinner = !!this.content.timeInterval;
  }

  ngAfterViewInit() {
    this.intervalId = setInterval(x => {
      this.showSpinner = false;
      this.renderGraph();
    }, this.content.timeInterval ? this.content.timeInterval : 400);

  }

  renderGraph() {
    const svg = d3.select('#' + this.bgid);
    if (svg) {
      clearInterval(this.intervalId);
      generateBarGraph(svg, this.content);
    }
  }

}
