import {Component, ElementRef, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {Message} from '../../models/message';
import {RestService} from '../../service/rest/rest.service';

@Component({
  selector: 'app-message-form',
  templateUrl: './message-form.component.html',
  styleUrls: ['./message-form.component.css']
})
export class MessageFormComponent implements OnInit {
  @Output() msgemitter = new EventEmitter<Message>();
  constructor(private restservice: RestService) { }
  public showBar = false;
  @ViewChild('chatmsg') chatmsg: ElementRef;

  ngOnInit() {
    this.restservice.requestEvnt.subscribe(val => { this.toggleProgressBar(val); });
  }
  toggleProgressBar(showBar) {
    this.showBar = showBar;
  }

  sendMessage() {
    const msg = this.chatmsg.nativeElement.value.trim();
    if (msg) {
      const curTime = new Date();
      const message = new Message(msg, 'User', 'user', curTime);
      this.chatmsg.nativeElement.value = '';
      this.msgemitter.emit(message);
    }
  }

  keyDownFunction() {
    this.sendMessage();
  }
}
