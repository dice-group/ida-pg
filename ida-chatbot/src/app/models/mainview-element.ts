import {TabElement} from './tab-element';

export class MainviewElement {
  id: number;
  dataset: object;
  extraTabs: TabElement[];

  constructor(id: number, dataset: object, extraTabs = []) {
    this.id = id;
    this.dataset = dataset;
    this.extraTabs = extraTabs;
  }

  public getExtraTabs() {
    return this.extraTabs;
  }
}
