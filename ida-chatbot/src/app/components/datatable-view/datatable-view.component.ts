import {Component, Input, OnInit} from '@angular/core';
import {SidebarElement} from '../../models/sidebar-element';

@Component({
  selector: 'app-datatable-view',
  templateUrl: './datatable-view.component.html',
  styleUrls: ['./datatable-view.component.css']
})
export class DatatableViewComponent implements OnInit {
  @Input('data')
  public tableData: object;
  public fields: string[] = [];

  constructor() {

  }

  ngOnInit() {
    this.tableData = JSON.parse(this.tableData.toString());
    /*console.log(this.tableData);*/
    for (const x in this.tableData[0]) {
      if (x != null) {
        this.fields.push(x);
      }
    }
  }

}
