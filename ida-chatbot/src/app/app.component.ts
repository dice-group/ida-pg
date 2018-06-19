import {Component, ElementRef, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {Message} from './models/message';
import {ResponseBean} from './models/response-bean';
import {SidebarComponent} from './components/sidebar/sidebar.component';
import {MainviewElement} from './models/mainview-element';
import {SidebarElement} from './models/sidebar-element';
import {ChatboxComponent} from './components/chatbox/chatbox.component';
import {RestService} from './service/rest/rest.service';
import {DataViewContainerComponent} from './components/data-view-container/data-view-container.component';
import {TabElement} from './models/tab-element';
import {UniqueIdProviderService} from './service/misc/unique-id-provider.service';
import {TabType} from './enums/tab-type.enum';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  idCount = 1;
  title = 'app';
  public introSideItem = new SidebarElement(0, 'Introduction', 'intro');
  private sidebarItems: SidebarElement[] = [this.introSideItem];
  private mainViewItems: MainviewElement[] = [];
  @ViewChild(ChatboxComponent)
  private chatboxComp: ChatboxComponent;
  @ViewChildren(DataViewContainerComponent)
  private dataVwCompList: QueryList<DataViewContainerComponent>;
  @ViewChild(SidebarComponent)
  private sbComp: SidebarComponent;
  public activeItem = 0;

  AppComponent() {
  }

  constructor(private restservice: RestService, private uis: UniqueIdProviderService) {

  }

  public actionHandler(resp: ResponseBean) {
    if (resp.actnCode === 1) {
      const newId = this.idCount++;
      // load the dataset
      const sdbEle = new SidebarElement(newId, resp.payload.label, resp.payload.dsName);
      this.sidebarItems.push(sdbEle);
      const mvEle = new MainviewElement(newId, resp.payload.dataset);
      this.mainViewItems.push(mvEle);
      this.activeItem = newId;
    } else if (resp.actnCode === 2) {
      // Open new tab
      const newTab = new TabElement(this.uis.getUniqueId(), 'Force Directed Graph', TabType.FDG, resp.payload.fdgData, true, true);
      for (const mvwItem of this.mainViewItems) {
        if (Number(resp.payload.actvScrId) === mvwItem.id) {
          // Add a tab to extra tabs
          mvwItem.extraTabs.push(newTab);
          this.getActiveMainView().focusLastTab();
          break;
        }
      }
    } else if (resp.actnCode === 3) {
      // Open new tab
      const newTab = new TabElement(this.uis.getUniqueId(), 'Bar Graph', TabType.BG, resp.payload.bgData, true, true);
      for (const mvwItem of this.mainViewItems) {
        if (Number(resp.payload.actvScrId) === mvwItem.id) {
          // Add a tab to extra tabs
          mvwItem.extraTabs.push(newTab);
          this.getActiveMainView().focusLastTab();
          break;
        }
      }
    }
  }

  addNewUserMessage(message: Message) {
    this.chatboxComp.addNewMessage(message);
    let actvTbl = '';
    let actvDs = '';
    if (this.getActiveMainView() && this.sbComp) {
      actvTbl = this.getActiveMainView().getActiveDataTableName();
      actvDs = this.sbComp.getActiveDatasetName();
    }
    const prmobj = {
      msg: message.content,
      actvScrId: this.activeItem,
      actvTbl: actvTbl == null ? '' : actvTbl,
      actvDs: actvDs == null ? '' : actvDs
    };
    // Send the message to server
    this.restservice.getRequest('/message/sendmessage', prmobj).subscribe(resp => this.processBotResponse(resp));
  }

  processBotResponse(resp: ResponseBean) {
    const msg: Message = new Message(resp.chatmsg, 'Assistant', 'chatbot', new Date());
    // Putting delay to make responses look natural
    setTimeout(() => this.chatboxComp.addNewMessage(msg), 1000);
    if (resp.actnCode > 0) {
      // load the dataset
      this.actionHandler(resp);
    }
  }

  public getActiveMainView(): DataViewContainerComponent {
    let activeMainDtVw = null;
    this.dataVwCompList.forEach((mvEntry) => {
      if (mvEntry.item.id === this.activeItem) {
        activeMainDtVw = mvEntry;
      }
    });
    return activeMainDtVw;
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
