import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {SidebarElement} from '../../models/sidebar-element';
import {MatPaginator, MatSort, MatTableDataSource} from '@angular/material';

@Component({
  selector: 'app-datatable-view',
  templateUrl: './datatable-view.component.html',
  styleUrls: ['./datatable-view.component.css']
})
export class DatatableViewComponent implements OnInit {
  @Input('data')
  public tableData: object[];
  public fields: string[] = [];
  public dataSource: MatTableDataSource<object>;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  constructor() {

  }

  ngOnInit() {
    this.tableData = JSON.parse(this.tableData.toString());
    this.dataSource = new MatTableDataSource(this.tableData);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    /*console.log(this.tableData);*/
    for (const x in this.tableData[0]) {
      if (x != null) {
        this.fields.push(x);
      }
    }
  }

  applyFilter(filterValue: string) {
    filterValue = filterValue.trim(); // Remove whitespace
    filterValue = filterValue.toLowerCase(); // Datasource defaults to lowercase matches
    this.dataSource.filter = filterValue;
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

}
