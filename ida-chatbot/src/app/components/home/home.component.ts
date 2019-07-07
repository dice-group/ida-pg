import {Component, OnInit, ElementRef, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {Message} from '../../models/message';
import {ResponseBean} from '../../models/response-bean';
import {SidebarComponent} from '../../components/sidebar/sidebar.component';
import {MainviewElement} from '../../models/mainview-element';
import {SidebarElement} from '../../models/sidebar-element';
import {ChatboxComponent} from '../../components/chatbox/chatbox.component';
import {RestService} from '../../service/rest/rest.service';
import {DataViewContainerComponent} from '../../components/data-view-container/data-view-container.component';
import {StoryboardDialogComponent} from '../../dialogs/storyboard/storyboard.dialog.component';
import {TabElement} from '../../models/tab-element';
import {UniqueIdProviderService} from '../../service/misc/unique-id-provider.service';
import {TabType} from '../../enums/tab-type.enum';
import {IdaEventService} from '../../service/event/ida-event.service';
import {MatDialog} from '@angular/material';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {environment} from '../../../environments/environment';
import {UserService} from '../../service/user/user.service';
import {Router} from '@angular/router';


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  idCount = 2;
  title = 'app';
  isHidden = true;
  public introSideItem = new SidebarElement(0, 'Introduction', 'intro');
  public ontologySideItem = new SidebarElement(1, 'Ontology Explorer', 'ontology');
  public activeItem = 0;
  private sidebarItems: SidebarElement[] = [this.introSideItem, this.ontologySideItem];
  private mainViewItems: MainviewElement[] = [];

  @ViewChild(ChatboxComponent)
  private chatboxComp: ChatboxComponent;
  @ViewChildren(DataViewContainerComponent)
  private dataVwCompList: QueryList<DataViewContainerComponent>;
  @ViewChild(SidebarComponent)
  private sbComp: SidebarComponent;

  constructor(private restservice: RestService, private uis: UniqueIdProviderService,
              private ies: IdaEventService, private userservice: UserService, private router: Router,
              public dialog: MatDialog, private sanitizer: DomSanitizer) {
    ies.dtTblEvnt.subscribe((reqTbl) => {
      this.getDataTable(reqTbl);
    });
  }

  ngOnInit() {
    this.checkLoggedIn();
  }

  HomeComponent() {
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
      const newTab = new TabElement(this.uis.getUniqueId(), 'Force Directed Graph', TabType.FDG, resp.payload.actvTbl,
        resp.payload.fdgData, true, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 3) {
      // Open new tab with Bar Graph
      const newTab = new TabElement(this.uis.getUniqueId(), 'Bar Graph', TabType.BG, resp.payload.actvTbl, resp.payload.bgData, true, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 4) {
      // Open new tab with DataTable
      const newTab = new TabElement(this.uis.getUniqueId(), resp.payload.tabLabel, TabType.DTTBL, '', resp.payload.clusterData, true, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 5) {
      // Open new tab with DataTable
      const newTab = new TabElement(this.uis.getUniqueId(), resp.payload.actvTbl, TabType.DTTBL, '', resp.payload.dataTable, true, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 7) {
      // Open new tab with DataTable
      const newTab = new TabElement(this.uis.getUniqueId(), 'Venn Diagram', TabType.VENND, resp.payload.actvTbl,
        resp.payload.vennDiagramData, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 8) {
      // Open new tab with DataTable
      const newTab = new TabElement(this.uis.getUniqueId(), 'Geospatial Diagram', TabType.GSD, resp.payload.actvTbl,
        resp.payload, true, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 9) {
      // Open new tab with DataTable
      const newTab = new TabElement(this.uis.getUniqueId(), 'Sequence Sun Burst diagram', TabType.SSB, resp.payload.actvTbl,
        resp.payload.ssbDiagramData, true, true);
      this.addNewTab(newTab, resp);
    } else if (resp.actnCode === 11) {
        // this.openPopup(resp.payload.storyUrl);
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
    if (resp.actnCode === 11) {
      content = 'Here is your Storyboard URL: ' +
        '<a href=' + environment.chatbotBase + '/getstory?id=' + resp.payload.storyUuid + ' target="_blank"> ' +
         environment.chatbotBase + '/getstory?id=' + resp.payload.storyUuid + '</a>';
    } else {
      content = resp.chatmsg;
    }
    const msg: Message = new Message(this.cleanResponse(content), 'Assistant', 'chatbot', new Date());
    // Putting delay to make responses look natural
    setTimeout(() => this.chatboxComp.addNewMessage(msg), 300);
    if (resp.actnCode > 0) {
      // load the dataset
      this.actionHandler(resp);
    }
  }

  cleanResponse(content) {
    content = content.replace('pass == pass => ', '');
    content = content.replace('fail == pass => ', '');
    return content;
  }

  // openPopup(url) {
  //   this.dialog.open(StoryboardDialogComponent, {
  //     data: {
  //       url: url
  //     }
  //   });
  // }

  checkLoggedIn() {
    this.restservice.getRequest('/auth/check-login', {}).subscribe(resp => {
      const returnResp = this.userservice.processUserResponse(resp);
      if (returnResp.status === true) {
        this.isHidden = false;
      } else {
        this.router.navigate(['login']);
      }
    });
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
