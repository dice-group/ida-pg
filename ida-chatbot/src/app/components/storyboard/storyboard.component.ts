import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {RestService} from '../../service/rest/rest.service';
import {MainviewElement} from '../../models/mainview-element';
import {TabElement} from '../../models/tab-element';
import {TabType} from '../../enums/tab-type.enum';
import {forkJoin} from 'rxjs';

@Component({
  selector: 'app-storyboard',
  templateUrl: './storyboard.component.html',
  styleUrls: ['./storyboard.component.css']
})
export class StoryboardComponent implements OnInit {

  private mainViewItems: MainviewElement[] = [];
  storyUID: string;
  constructor(private route: ActivatedRoute, private restservice: RestService) {
    this.route.queryParams.subscribe(params => {
      this.storyUID = params['id'];
    });
  }

  ngOnInit() {
    this.callbackendAPI();
  }

  public getMainviewItems() {
    return this.mainViewItems;
  }

  callbackendAPI() {
    // call to get Table and visualization data
    const prmobj = {};
    this.restservice.getRequest('/getstory?id=' + this.storyUID, prmobj).subscribe(
      resp => {
        const mvEle = new MainviewElement(0, resp.payload.actvTbl, null);
        this.mainViewItems.push(mvEle);
        const tab = new TabElement(1, resp.payload.actvTbl, TabType.DTTBL, '', resp.payload.dataTable, true, true);
        this.mainViewItems[0].tabArr.push(tab);

        if (resp.payload.actvVs === 'Bar Graph') {
          this.generateBarGraph(resp.payload);
        } else if (resp.payload.actvVs === 'Force Directed Graph') {
          this.generateForceDirectedGraph(resp.payload);
        } else if (resp.payload.actvVs === 'Venn Diagram') {
          this.generateVennDiagram(resp.payload);
        } else if (resp.payload.actvVs === 'Geospatial Diagram') {
          this.generateGeospatialDiagram(resp.payload);
        } else if (resp.payload.actvVs === 'Sequence Sun Burst diagram') {
          this.generateSSBDiagram(resp.payload);
        }
      }
    );
  }

  generateBarGraph(payload) {
    const prmobj = {
      msg: 'I want a bar-graph visualisation for the current table',
      actvScrId: payload.actvScrId,
      actvTbl: payload.actvTbl,
      actvDs: payload.actvDs,
      actvVs: ''
    };
    // Init request for bra graph
    const a = this.restservice.getRequest('/message/sendmessage', prmobj);

    // x-axis parameter
    prmobj.msg = 'x-axis is ' + payload.actvVsData[0];
    const b = this.restservice.getRequest('/message/sendmessage', prmobj);

    // y-axis parameter
    prmobj.msg = 'y-axis is ' + payload.actvVsData[1];
    const c = this.restservice.getRequest('/message/sendmessage', prmobj);
    // Number of Items
    prmobj.msg = payload.actvVsData[2];
    const d = this.restservice.getRequest('/message/sendmessage', prmobj);

    forkJoin([a, b, c, d]).subscribe(([ra, rb, rc, rd]) => {
      rd.payload.bgData.timeInterval = 1500;
      const tab = new TabElement(1, payload.actvVs, TabType.BG, payload.actvTbl, rd.payload.bgData, true, true);
      this.mainViewItems[0].tabArr.push(tab);
    });
  }

  generateForceDirectedGraph(payload) {
    const prmobj = {
      msg: 'I would like a force directed graph visualization for the current table',
      actvScrId: payload.actvScrId,
      actvTbl: payload.actvTbl,
      actvDs: payload.actvDs,
      actvVs: ''
    };
    // Init request for force directed graph
    const a = this.restservice.getRequest('/message/sendmessage', prmobj);
    // source parameter
    prmobj.msg = 'Source node is ' + payload.actvVsData[0];
    const b = this.restservice.getRequest('/message/sendmessage', prmobj);
    // target parameter
    prmobj.msg = 'Target node is ' + payload.actvVsData[1];
    const c = this.restservice.getRequest('/message/sendmessage', prmobj);
    // strength parameter
    prmobj.msg = payload.actvVsData[2];
    const d = this.restservice.getRequest('/message/sendmessage', prmobj);

    forkJoin([a, b, c, d]).subscribe(([ra, rb, rc, rd]) => {
      rd.payload.fdgData.timeInterval = 4000;
      const tab = new TabElement(1, payload.actvVs, TabType.FDG, payload.actvTbl, rd.payload.fdgData, true, true);
      this.mainViewItems[0].tabArr.push(tab);
    });
  }

  generateVennDiagram(payload) {
    const prmobj = {
      msg: 'show me a venn diagram',
      actvScrId: payload.actvScrId,
      actvTbl: payload.actvTbl,
      actvDs: payload.actvDs,
      actvVs: ''
    };
    // Init request for venn diagram
    const a = this.restservice.getRequest('/message/sendmessage', prmobj);
    // parameters for venn diagram
    prmobj.msg = 'count of ' + payload.actvVsData[0] + ' wrt top ' + payload.actvVsData[1] + ' ' + payload.actvVsData[2];
    const b = this.restservice.getRequest('/message/sendmessage', prmobj);

    forkJoin([a, b]).subscribe(([ra, rb]) => {
      rb.payload.vennDiagramData.timeInterval = 400;
      const tab = new TabElement(1, payload.actvVs, TabType.VENND, payload.actvTbl, rb.payload.vennDiagramData, true, true);
      this.mainViewItems[0].tabArr.push(tab);
    });
  }

  generateGeospatialDiagram(payload) {
    const prmobj = {
      msg: 'show me the geo spatial diagram',
      actvScrId: payload.actvScrId,
      actvTbl: payload.actvTbl,
      actvDs: payload.actvDs,
      actvVs: ''
    };
    // Init request for geospatial diagram
    const a = this.restservice.getRequest('/message/sendmessage', prmobj);
    // parameters for geospatial diagram
    prmobj.msg = 'use ' + payload.actvVsData[0] + ' as latitude and ' + payload.actvVsData[1] + ' as longitude';
    const b = this.restservice.getRequest('/message/sendmessage', prmobj);

    forkJoin([a, b]).subscribe(([ra, rb]) => {
      const tab = new TabElement(1, payload.actvVs, TabType.GSD, payload.actvTbl, rb.payload.gsDiagramData, true, true);
      this.mainViewItems[0].tabArr.push(tab);
    });
  }

  generateSSBDiagram(payload) {
    const prmobj = {
      msg: 'show me the sequence sun burst diagram',
      actvScrId: payload.actvScrId,
      actvTbl: payload.actvTbl,
      actvDs: payload.actvDs,
      actvVs: ''
    };
    // Init request for sequence sun burst diagram
    const a = this.restservice.getRequest('/message/sendmessage', prmobj);
    // parameters for sequence sun burst diagram
    prmobj.msg = 'use ' + payload.actvVsData[0] + ' sequenced on ' + payload.actvVsData[1];
    const b = this.restservice.getRequest('/message/sendmessage', prmobj);

    forkJoin([a, b]).subscribe(([ra, rb]) => {
      const tab = new TabElement(1, payload.actvVs, TabType.SSB, payload.actvTbl, rb.payload.ssbDiagramData, true, true);
      this.mainViewItems[0].tabArr.push(tab);
    });
  }

}
