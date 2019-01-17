import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';

declare var deck;

declare function HexagonLayer(a): void;
declare function IconLayer(a): void;

@Component({
  selector: 'app-deckgl-hex-view',
  templateUrl: './deckgl-hex-view.component.html',
  styleUrls: ['./deckgl-hex-view.component.css']
})
export class DeckglHexViewComponent implements OnInit, AfterViewInit {
  @Input('data') public demDt;
  public deckContainerId;
  public intervalId: any;

  constructor(public uip: UniqueIdProviderService) {
    this.deckContainerId = 'deck-container' + this.uip.getUniqueId();
  }

  ngOnInit() {
    this.demDt = this.demDt;
  }

  ngAfterViewInit() {
    this.intervalId = setInterval(x => {
      this.renderGraph();
    }, 400);
  }

  isIconVisible(deckobj) {
    console.log(deckobj);
  }

  renderGraph () {
    clearInterval(this.intervalId);
    const deckobj = new deck.DeckGL({
      container: this.deckContainerId,
      mapboxApiAccessToken: '',
      mapStyle: 'https://maps.tilehosting.com/styles/positron/style.json?key=2SSrAclSqe0xz6m7VpSU',
      longitude: this.demDt.lon,
      latitude: this.demDt.lat,
      zoom: 9,
      bearing: 0,
      pitch: 40,
      minZoom: 5,
      maxZoom: 12,
      layers: [
        new HexagonLayer({
          id: 'hexagon-layer',
          data: this.demDt.gsDiagramData,
          pickable: true,
          extruded: true,
          radius: 300,
          elevationScale: 4,
          getPosition: d => d.COORDINATES,
          // onHover: ({object}) => setTooltip(`${object.centroid.join(', ')}\nCount: ${object.points.length}`)
          onHover: info => this.setTooltip(info)
        }),
        new IconLayer({
          id: 'icon-layer',
          data: this.demDt.gsDiagramData,
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
          sizeScale: 100,
          getPosition: d => d.COORDINATES,
          getIcon: d => 'marker',
          getColor: d => [Math.sqrt(d.exits), 140, 0],
          // onHover: ({object}) => setTooltip(`${object.name}\n${object.address}`)
        })
      ]
    });
  }

  setTooltip(object) {
    console.log(object);

    // const el = document.getElementById('tooltip');
    // if (object) {
    //   el.innerHTML = object.message;
    //   el.style.display = 'block';
    //   el.style.left = x;
    //   el.style.top = y;
    // } else {
    //   el.style.display = 'none';
    // }
  }
}
