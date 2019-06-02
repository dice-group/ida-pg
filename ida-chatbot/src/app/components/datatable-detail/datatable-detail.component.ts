import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {DatasetMetadata} from '../../models/dataset-metadata';
import {DatafileMetadata} from '../../models/datafile-metadata';
import {IdaEventService} from '../../service/event/ida-event.service';
import {MatAccordion} from '@angular/material';

declare function createV3RDFOntologyView();

@Component({
  selector: 'app-datatable-detail',
  templateUrl: './datatable-detail.component.html',
  styleUrls: ['./datatable-detail.component.css']
})
export class DatatableDetailComponent implements OnInit {
  @Input('dsdetail')
  public dsdetail: DatasetMetadata;
  @ViewChild('tableAcc')
  public tableAcc: MatAccordion;
  @ViewChild('colAcc')
  public colAcc: MatAccordion;
  public displayedColumns: string[] = ['displayName', 'fileName', 'rowCount', 'colCount'];
  public expandedElement: DatafileMetadata;
  public svgid: string ='svgid';

  constructor(private ies: IdaEventService) {
  }

  ngOnInit() {
  }

  ngAfterViewInit() {
    createV3RDFOntologyView();
  }
  
  requestDataTable(reqTbl: string) {
    this.ies.dtTblEvnt.emit(reqTbl);
  }

}
