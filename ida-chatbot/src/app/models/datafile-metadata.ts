import {DatacolumnMetadata} from './datacolumn-metadata';

export class DatafileMetadata {
  fileName: string;
  displayName: string;
  fileDesc: string;
  rowCount: number;
  colCount: number;
  fileColMd: DatacolumnMetadata[];
}
