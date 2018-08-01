import {TabElement} from './tab-element';
import {DatasetMetadata} from './dataset-metadata';

export class MainviewElement {
  id: number;
  dataset: object;
  extraTabs: TabElement[];
  dsMd: DatasetMetadata;

  constructor(id: number, dataset: object, dsMd: DatasetMetadata, extraTabs = []) {
    this.id = id;
    this.dataset = dataset;
    this.dsMd = dsMd;
    this.extraTabs = extraTabs;
  }

  public getExtraTabs() {
    return this.extraTabs;
  }
}
