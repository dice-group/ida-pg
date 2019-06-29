import {Component, Input, OnInit} from '@angular/core';
import {MainviewElement} from '../../models/mainview-element';
import {TabElement} from '../../models/tab-element';
import {TabType} from '../../enums/tab-type.enum';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';

@Component({
  selector: 'app-data-view-container',
  templateUrl: './data-view-container.component.html',
  styleUrls: ['./data-view-container.component.css']
})
export class DataViewContainerComponent implements OnInit {
  @Input('item')
  public item: MainviewElement;
  public object = Object;
  public activeTabIndex = 0;

  constructor(private uis: UniqueIdProviderService) {
  }

  ngOnInit() {
    // TODO: Create and push tabs inside mainviewelement
    const metaDt = this.item.dsMd;
    if (metaDt) {
      const tempTab = new TabElement(this.uis.getUniqueId(), metaDt.dsName + ' Schema', TabType.DSDET, '', metaDt, false);
      this.item.getTabArr().push(tempTab);
      this.focusLastTab();
    }
  }

  closeTab(tab: TabElement) {
    const ind = this.item.tabArr.indexOf(tab);
    if (ind > -1) {
      this.item.tabArr = this.item.tabArr.filter(item => item !== tab);
    }
  }

  public getActiveDataTableName() {
    const activeTab: TabElement = this.item.tabArr[this.activeTabIndex];
    if (activeTab.tabType === TabType.DTTBL) {
      // returning the associated filename
      return activeTab.label;
    } else {
      return activeTab.table;
    }
  }

  public getActiveVisualizationName() {
    const activeTab: TabElement = this.item.tabArr[this.activeTabIndex];
    if (activeTab.tabType === TabType.BG || activeTab.tabType === TabType.FDG || activeTab.tabType === TabType.VENND ||
      activeTab.tabType === TabType.GSD || activeTab.tabType === TabType.SSB) {
      return activeTab.label;
    }
    return null;
  }

  public focusLastTab() {
    this.activeTabIndex = this.item.tabArr.length - 1;
  }

}
