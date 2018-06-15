import {Component, Input, OnInit} from '@angular/core';
import {TabElement} from '../../models/tab-element';
import {TabType} from '../../enums/tab-type.enum';

@Component({
  selector: 'app-tab-view',
  templateUrl: './tab-view.component.html',
  styleUrls: ['./tab-view.component.css']
})
export class TabViewComponent implements OnInit {
  public tabType = TabType;
  @Input('tabElement')
  public tabElement: TabElement;

  constructor() {
    console.log('new tab loaded: ' + this.tabType);
  }

  ngOnInit() {
  }

}
