import {Component, Input, OnInit} from '@angular/core';
import {Message} from '../../models/message';
import {MainviewElement} from '../../models/mainview-element';

@Component({
  selector: 'app-data-view-container',
  templateUrl: './data-view-container.component.html',
  styleUrls: ['./data-view-container.component.css']
})
export class DataViewContainerComponent implements OnInit {
  @Input('item')
  public item: MainviewElement;
  public object = Object;
  constructor() { }

  ngOnInit() {
    // console.log(this.item);
  }

}
