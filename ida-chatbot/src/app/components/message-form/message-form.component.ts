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
  @ViewChild('chatmsg') chatmsg: ElementRef;

  constructor(private restservice: RestService) { }
  public showBar = false;
  ngOnInit() {
    this.restservice.requestEvnt.subscribe(val => { this.toggleProgressBar(val); });
  }
  toggleProgressBar(showBar) {
    this.showBar = showBar;
  }

  sendMessage(msg: string) {
    if (msg == null || !msg) {
      return false;
    }
    const curTime = new Date();
    const message = new Message(msg, 'User', 'user', curTime);
    this.msgemitter.emit(message);
  }

  keyDownFunction(event, msg: string) {
    if (event.keyCode === 13) {
      this.sendMessage(msg);
    }
  }

  getVoiceToMsg(msg) {
    this.chatmsg.nativeElement.value = msg;
  }
}
