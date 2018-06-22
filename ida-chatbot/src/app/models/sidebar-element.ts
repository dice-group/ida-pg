export class SidebarElement {
  id: number;
  label: string;
  dsName: string;
  constructor(id: number, label: string, dsName: string) {
    this.id = id;
    this.label = label;
    this.dsName = dsName;
  }
}
