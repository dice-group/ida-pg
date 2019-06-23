import {TabType} from '../enums/tab-type.enum';

export class TabElement {
  id: number;
  label: string;
  tabType: TabType;
  table: string;
  content: any;
  closable: boolean;
  active: boolean;
  constructor(id: number, label: string, tabType: TabType, table: string, content: any, closable = false, active = false) {
    this.id = id;
    this.label = label;
    this.tabType = tabType;
    this.table = table;
    this.content = content;
    this.closable = closable;
    this.active = active;
  }
}
