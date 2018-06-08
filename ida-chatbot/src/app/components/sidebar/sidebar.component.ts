import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChange} from '@angular/core';
import {Message} from '../../models/message';
import {SidebarElement} from '../../models/sidebar-element';
import {ResponseBean} from '../../models/response-bean';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit{
  @Input('items')
  public items: SidebarElement[];
  @Output() activeItmEmitter = new EventEmitter<number>();
  @Input('activeItem')
  public activeItem: number;

  constructor() {
  }

  ngOnInit() {
  }

  /*ngOnChanges(changes: { [propKey: string]: SimpleChange }) {
    for (const propName in changes) {
      if (propName === 'activeItem') {
        /!*setTimeout(() => function () {
          this.activeItem = changes[propName].currentValue;
        }, 500);*!/
        this.activeItem = changes[propName].currentValue;
      }
    }
  }*/

  clickItem(id: number) {
    this.activeItmEmitter.emit(id);
  }

}
