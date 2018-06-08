import {Component} from '@angular/core';
import {Message} from './models/message';
import {ResponseBean} from './models/response-bean';
import {SidebarComponent} from './components/sidebar/sidebar.component';
import {MainviewElement} from './models/mainview-element';
import {SidebarElement} from './models/sidebar-element';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  idCount = 0;
  title = 'app';
  private sidebarItems: SidebarElement[] = [];
  private mainViewItems: MainviewElement[] = [];
  public dummyMvw = new MainviewElement(-1, {});
  public activeItem = -1;

  AppComponent() {
  }

  public actionHandler(resp: ResponseBean) {
    if (resp.actnCode === 1) {
      const newId = this.idCount++;
      // load the dataset
      const sdbEle = new SidebarElement(newId, resp.payload.label);
      this.sidebarItems.push(sdbEle);
      const mvEle = new MainviewElement(newId, resp.payload.dataset);
      this.mainViewItems.push(mvEle);
      this.activeItem = newId;
    }
  }

  public getMainviewItems() {
    return this.mainViewItems;
  }

  public getSidebarItems() {
    return this.sidebarItems;
  }

  public changeActiveItem(newId: number) {
    this.activeItem = newId;
  }

  public getActiveItem() {
    return this.activeItem;
  }
}
