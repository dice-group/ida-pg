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
  public activeTabIndex = 0;
  public datasetCount = 0;

  constructor() {
  }

  ngOnInit() {
    if (this.item.dataset) {
      this.datasetCount = this.object.keys(this.item.dataset).length;
    }
    // console.log(this.item);
  }

  closeTab(tab: TabElement) {
    const ind = this.item.extraTabs.indexOf(tab);
    if (ind > -1) {
      this.item.extraTabs = this.item.extraTabs.filter(item => item !== tab);
    }
  }

  public getActiveDataTableName() {
    if (this.activeTabIndex < this.datasetCount) {
      // returning the associated filename
      return this.object.keys(this.item.dataset)[this.activeTabIndex];
    }
    return null;
  }

  public focusLastTab() {
    this.activeTabIndex = this.datasetCount + this.item.extraTabs.length - 1;
  }

}
