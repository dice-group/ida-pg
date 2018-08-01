import {Component, Input, OnInit} from '@angular/core';
import {MainviewElement} from '../../models/mainview-element';
import {DatasetMetadata} from '../../models/dataset-metadata';

@Component({
  selector: 'app-datatable-detail',
  templateUrl: './datatable-detail.component.html',
  styleUrls: ['./datatable-detail.component.css']
})
export class DatatableDetailComponent implements OnInit {
  @Input('dsdetail')
  public dsdetail: DatasetMetadata;
  constructor() { }

  ngOnInit() {
  }

}
