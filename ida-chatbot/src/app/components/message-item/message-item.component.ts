import {Component, Input, OnInit} from '@angular/core';
import {Message} from '../../models/message';

@Component({
  selector: 'app-message-item',
  templateUrl: './message-item.component.html',
  styleUrls: ['./message-item.component.css']
})
export class MessageItemComponent implements OnInit {
  @Input('message')
  public message: Message;
  constructor() { }

  ngOnInit() {
    /*console.log(this.message);*/
  }

}
