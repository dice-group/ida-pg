import {Component, ElementRef, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {Message} from '../../models/message';

@Component({
  selector: 'app-message-form',
  templateUrl: './message-form.component.html',
  styleUrls: ['./message-form.component.css']
})
export class MessageFormComponent implements OnInit {
  @Output() msgemitter = new EventEmitter<Message>();
  constructor() { }

  ngOnInit() {
  }

  sendMessage(msg: string) {
    if (msg == null || !msg) {
      return false;
    }
    const curTime = new Date();
    const message = new Message(msg, 'User', 'user', curTime);
    this.msgemitter.emit(message);
  }

  /*keyDownFunction(event, btn) {
    if (event.keyCode === 13) {
      btn._elementRef.nativeElement.click();
    }
  }*/
}
