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
  public tempContent: any;
  constructor(public uip: UniqueIdProviderService) {
    this.bgid = 'bg' + this.uip.getUniqueId();
    this.tempContent = {
      xaxisname : 'cityName',
      yaxisname : 'avgrent',
      keys : [ 'cityName' ],
      baritems : [ {
        cityName : 'Berlin',
        avgrent : 2600,
      }, {
        cityName : 'Paris',
        avgrent : 4000
      }, {
        cityName : 'Delhi',
        avgrent : 2500
      }, {
        cityName : 'Bengaluru',
        avgrent : 2000
      }, {
        cityName : 'Paderborn',
        avgrent : 2450
      }, {
        cityName : 'Palika Bazaar',
        avgrent : 150
      } ]

    };
  }

  ngOnInit() {
  }

  ngAfterViewInit() {
    this.intervalId = setInterval(x => {
      this.renderGraph();
    }, 400);

  }

  renderGraph() {
    const svg = d3.select('#' + this.bgid);
    if (svg) {
      clearInterval(this.intervalId);
      generateBarGraph(svg, this.tempContent);
    }
  }

}
