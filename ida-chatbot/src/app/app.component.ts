import {Component, ElementRef, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {Message} from './models/message';
import {ResponseBean} from './models/response-bean';
import {SidebarComponent} from './components/sidebar/sidebar.component';
import {StoryboardDialogComponent} from './dialogs/storyboard/storyboard.dialog.component';
import {MainviewElement} from './models/mainview-element';
import {SidebarElement} from './models/sidebar-element';
import {ChatboxComponent} from './components/chatbox/chatbox.component';
import {RestService} from './service/rest/rest.service';
import {DataViewContainerComponent} from './components/data-view-container/data-view-container.component';
import {TabElement} from './models/tab-element';
import {UniqueIdProviderService} from './service/misc/unique-id-provider.service';
import {TabType} from './enums/tab-type.enum';
import {IdaEventService} from './service/event/ida-event.service';
import {MatDialog} from "@angular/material";
import {DomSanitizer} from '@angular/platform-browser';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  idCount = 1;
  title = 'app';
  public introSideItem = new SidebarElement(0, 'Introduction', 'intro');
  public activeItem = 0;
  private sidebarItems: SidebarElement[] = [this.introSideItem];
  private mainViewItems: MainviewElement[] = [];

  @ViewChild(ChatboxComponent)
  private chatboxComp: ChatboxComponent;
  @ViewChildren(DataViewContainerComponent)
  private dataVwCompList: QueryList<DataViewContainerComponent>;
  @ViewChild(SidebarComponent)
  private sbComp: SidebarComponent;

  constructor(private restservice: RestService, private uis: UniqueIdProviderService, private ies: IdaEventService, public dialog: MatDialog, private sanitizer:DomSanitizer) {
    ies.dtTblEvnt.subscribe((reqTbl) => {
      this.getDataTable(reqTbl);
    });
  }

  AppComponent() {
  }

  public actionHandler(resp: ResponseBean) {
    if (resp.actnCode === 1) {
      const newId = this.idCount++;
      // load the dataset
      const sdbEle = new SidebarElement(newId, resp.payload.label, resp.payload.dsName);
      this.sidebarItems.push(sdbEle);
      const mvEle = new MainviewElement(newId, resp.payload.dataset, resp.payload.dsMd);
      this.mainViewItems.push(mvEle);
      this.activeItem = newId;
    } else if (resp.actnCode === 2) {
      // Open new tab with Force Directed Graph
      const newTab = new TabElement(this.uis.getUniqueId(), 'Force Directed Graph', TabType.FDG, resp.payload.fdgData, true, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 3) {
      // Open new tab with Bar Graph
      const newTab = new TabElement(this.uis.getUniqueId(), 'Bar Graph', TabType.BG, resp.payload.bgData, true, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 4) {
      // Open new tab with DataTable
      const newTab = new TabElement(this.uis.getUniqueId(), resp.payload.tabLabel, TabType.DTTBL, resp.payload.clusterData, true, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 5) {
      // Open new tab with DataTable
      const newTab = new TabElement(this.uis.getUniqueId(), resp.payload.actvTbl, TabType.DTTBL, resp.payload.dataTable, true, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 7) {
      // Open new tab with DataTable
      const newTab = new TabElement(this.uis.getUniqueId(), resp.payload.actvTbl, TabType.VENND, resp.payload.vennDiagramData, true, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 8) {
      // Open new tab with DataTable
      const newTab = new TabElement(this.uis.getUniqueId(), resp.payload.actvTbl, TabType.GSD, resp.payload, true, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 9) {
      // Open new tab with DataTable
      const newTab = new TabElement(this.uis.getUniqueId(), resp.payload.actvTbl, TabType.SSB, resp.payload.ssbDiagramData, true, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 11) {
        this.openPopup(resp.payload.storyUrl);
    }
  }

  addNewTab(newTab: TabElement, resp: ResponseBean) {
    for (const mvwItem of this.mainViewItems) {
      if (Number(resp.payload.actvScrId) === mvwItem.id) {
        // Add a tab to extra tabs
        mvwItem.tabArr.push(newTab);
        this.getActiveMainView().focusLastTab();
        break;
      }
    }
  }

  addNewUserMessage(message: Message) {
    this.chatboxComp.addNewMessage(message);
    let actvTbl = '';
    let actvDs = '';
    let actvVs = '';
    if (this.getActiveMainView() && this.sbComp) {
      actvTbl = this.getActiveMainView().getActiveDataTableName();
      actvDs = this.sbComp.getActiveDatasetName();
      actvVs = this.getActiveMainView().getActiveVisualizationName();
    }
    const prmobj = {
      msg: message.content,
      actvScrId: this.activeItem,
      actvTbl: actvTbl == null ? '' : actvTbl,
      actvDs: actvDs == null ? '' : actvDs,
      actvVs: actvVs == null ? '' : actvVs
    };
    // Send the message to server
    this.restservice.getRequest('/message/sendmessage', prmobj).subscribe(resp => this.processBotResponse(resp));
  }

  getDataTable(reqTbl: string) {
    let actvDs = '';
    if (this.getActiveMainView() && this.sbComp) {
      actvDs = this.sbComp.getActiveDatasetName();
    }
    const prmobj = {
      actvScrId: this.activeItem,
      actvTbl: reqTbl == null ? '' : reqTbl,
      actvDs: actvDs == null ? '' : actvDs
    };
    // Send the message to server
    this.restservice.getRequest('/message/getdatatable', prmobj).subscribe(resp => this.processBotResponse(resp));
  }

  processBotResponse(resp: ResponseBean) {
    this.restservice.requestEvnt.emit(false);
    let content;
    if(resp.actnCode === 11){
      // content = "here is your Storyboard URL: <br><br><a href="+this.sanitize(resp.payload.storyUrl)+" target='_blank'>"+resp.payload.storyUrl+"</a>";
    } else{
      content = resp.chatmsg;
    }
    const msg: Message = new Message(content, 'Assistant', 'chatbot', new Date());
    // Putting delay to make responses look natural
    setTimeout(() => this.chatboxComp.addNewMessage(msg), 300);
    if (resp.actnCode > 0) {
      // load the dataset
      this.actionHandler(resp);
    }
  }

  openPopup(url){
    this.dialog.open(StoryboardDialogComponent, {
      data: {
        url: url
      }
    });
  }

  sanitize(url:string){
    // return this.sanitizer.bypassSecurityTrustUrl(window.location.protocol + '//' +url).changingThisBreaksApplicationSecurity;
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
