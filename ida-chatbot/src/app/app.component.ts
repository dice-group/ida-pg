import {Component, ElementRef, ViewChild} from '@angular/core';
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
  public introSideItem = new SidebarElement(0, 'Introduction');
  private sidebarItems: SidebarElement[] = [this.introSideItem];
  private mainViewItems: MainviewElement[] = [];
  @ViewChild(ChatboxComponent)
  private chatboxComp: ChatboxComponent;
  @ViewChild(DataViewContainerComponent)
  private dvwComp: DataViewContainerComponent;
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
      const sdbEle = new SidebarElement(newId, resp.payload.label);
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
          this.dvwComp.focusLastTab();
          break;
        }
      }
    }
  }

  addNewUserMessage(message: Message) {
    this.chatboxComp.addNewMessage(message);
    let actvTbl = '';
    let actvDs = '';
    if (this.dvwComp && this.sbComp) {
      actvTbl = this.dvwComp.getActiveDataTableName();
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
