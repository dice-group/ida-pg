import {DatafileMetadata} from './datafile-metadata';

export class DatasetMetadata {
  dsName: string;
  dsDesc: string;
  filesMd: DatafileMetadata[];
}
