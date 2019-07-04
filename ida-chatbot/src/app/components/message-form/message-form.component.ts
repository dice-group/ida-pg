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

  public showBar = false;
  msgs_history = []; // This will maintain history of all messages
  msgs_tracker = 0; // This will keep position of current message from history
  constructor(private restservice: RestService) { }

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
      this.msgs_history.push(msg);
      this.msgemitter.emit(message);
      this.msgs_tracker = this.msgs_history.length;
    }
  }

  getVoiceToMsg(msg) {
    this.chatmsg.nativeElement.value = msg;
  }

  /**
   * This will let user to iterate over messages from history like
   * Terminal
   * Remember, its just in Browser memory so it will go away with
   * Page refresh. We can later think about it to store in DB
   *
   * @param direction
   */
  iterateMsgs(direction) {
    // Checking if we have any message in history or not
    if (this.msgs_history.length) {
      if (direction === 'up' && this.msgs_tracker > 0) {
        this.chatmsg.nativeElement.value = this.msgs_history[--this.msgs_tracker];
      } else if (direction === 'down' && this.msgs_tracker < this.msgs_history.length - 1) {
        this.chatmsg.nativeElement.value = this.msgs_history[++this.msgs_tracker];
      }
    }
  }
}
