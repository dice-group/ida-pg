import {EventEmitter, Injectable} from '@angular/core';
import {Message} from '../../models/message';

@Injectable({
  providedIn: 'root'
})
export class IdaEventService {
  dtTblEvnt = new EventEmitter<string>();
  constructor() { }
}
