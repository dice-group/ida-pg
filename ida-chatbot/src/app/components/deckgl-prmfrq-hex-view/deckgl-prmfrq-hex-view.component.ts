import { Component, OnInit, AfterViewInit, Input } from '@angular/core';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';
import { d } from '@angular/core/src/render3';

declare var deck;

declare function HexagonLayer(a): void;
declare function IconLayer(a): void;

@Component({
  selector: 'app-deckgl-prmfrq-hex-view',
  templateUrl: './deckgl-prmfrq-hex-view.component.html',
  styleUrls: ['./deckgl-prmfrq-hex-view.component.css']
})
export class DeckglPrmfrqHexViewComponent implements OnInit, AfterViewInit {
  @Input('data') public demDt;
  public deckContainerId;
  public intervalId: any;

  ngOnInit() {
    this.demDt = this.demDt;
  } 
  
  ngAfterViewInit() {
    this.intervalId = setInterval(x => {
      this.renderGraph();
    }, 400);
  }

  constructor(public uip: UniqueIdProviderService) {
    this.deckContainerId = 'deck-container' + this.uip.getUniqueId();
  }

  isIconVisible(deckobj) {
    console.log(deckobj);
  }

  updateTooltip({x, y, object}) {
    const tooltip = document.getElementById('gstt');
    y = y - 15;
    x = x - 10
    if (object) {
      tooltip.style.top = `${y}px`;
      tooltip.style.left = `${x}px`;
      tooltip.innerHTML = object.points.length + ' Entry(s) <br>';
    } else {
      tooltip.innerHTML = '';
    }
  }

  setTooltip(object) {
    console.log(object);
  }

  renderGraph(){
    clearInterval(this.intervalId);
    const deckobj = new deck.DeckGL({
      container: this.deckContainerId,
      mapboxApiAccessToken: '',
      mapStyle: 'https://maps.tilehosting.com/styles/positron/style.json?key=2SSrAclSqe0xz6m7VpSU',
      longitude: this.demDt.lon,
      latitude: this.demDt.lat,
      zoom: 5,
      bearing: 0,
      pitch: 100,
      minZoom: 5,
      maxZoom: 12,
      layers: [
        new HexagonLayer({
          id: 'hexagon-layer',
          data: this.demDt.prmfrqDiagramData,
          pickable: true,
          extruded: true,
          radius: 10000,
          elevationScale: 200,
          getPosition: d => [d.COORDINATES[0], d.COORDINATES[1]],
          onHover: this.updateTooltip
        }),
        new IconLayer({
          id: 'icon-layer',
          data: this.demDt.prmfrqDiagramData,
          pickable: true,
          iconAtlas: '/assets/icon-atlas.png',
          iconMapping: {
            marker: {
              x: 0,
              y: 0,
              width: 500,
              height: 500,
              anchorY: 250,
              mask: true
            }
          },
          sizeScale: 0,
          getPosition: d => [d.COORDINATES[0], d.COORDINATES[1]],
          getIcon: d => 'marker',
          getColor: d => [Math.sqrt(d.exits), 140, 0],
        })
      ]
    });
  }

}
