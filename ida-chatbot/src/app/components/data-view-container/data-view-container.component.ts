import {Component, Input, OnInit} from '@angular/core';
import {Message} from '../../models/message';
import {MainviewElement} from '../../models/mainview-element';
import {TabElement} from '../../models/tab-element';
import {TabType} from '../../enums/tab-type.enum';

@Component({
  selector: 'app-data-view-container',
  templateUrl: './data-view-container.component.html',
  styleUrls: ['./data-view-container.component.css']
})
export class DataViewContainerComponent implements OnInit {
  @Input('item')
  public item: MainviewElement;
  public object = Object;
  public tempTab = new TabElement(1, 'TempTab', TabType.GEN, null, true);
  public extraTabs: TabElement[] = [this.tempTab, this.tempTab, this.tempTab, this.tempTab, this.tempTab, this.tempTab, this.tempTab];
  public activeTabIndex = 0;

  constructor() {
  }

  ngOnInit() {
    // console.log(this.item);
  }

  closeTab(tab: TabElement) {
    const ind = this.extraTabs.indexOf(tab);
    if (ind > -1) {
      this.extraTabs = this.extraTabs.filter(item => item !== tab);
    }
  }

}
