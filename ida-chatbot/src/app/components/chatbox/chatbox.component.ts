import {AfterViewChecked, Component, ElementRef, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {Message} from '../../models/message';
import {RestService} from '../../service/rest/rest.service';
import {ResponseBean} from '../../models/response-bean';
import {ChatBoxAdapter} from './chatbox-adapter';
import {IChatController} from 'ng-chat';

@Component({
  selector: 'app-chatbox',
  templateUrl: './chatbox.component.html',
  styleUrls: ['./chatbox.component.css']
})
export class ChatboxComponent implements OnInit, AfterViewChecked {
  @ViewChild('scrollMe') private myScrollContainer: ElementRef;
  @Output() actnEmitter = new EventEmitter<Message>();
  curDate = new Date();
  msg1: Message = new Message('Hello, I am your data assistant. How can I help you?', 'Assistant', 'chatbot', this.curDate);
  /*msg2: Message = new Message('Hi! Could you please load the City dataset.', 'User', 'user', this.curDate);*/
  /*msg3: Message = new Message('Yes I can, Please wait a moment.', 'Assistant', 'chatbot', this.curDate);
  msg4: Message = new Message('ok', 'Assistant', 'User', this.curDate);
  msg5: Message = new Message('City dataset is loaded.', 'Assistant', 'chatbot', this.curDate);
  msg6: Message = new Message('Thank you!', 'User', 'user', this.curDate);*/
  messages: Message[] = [this.msg1];

  constructor() {
  }

  ngOnInit() {
    // this.scrollToBottom();
  }

  ngAfterViewChecked() {
    // this.scrollToBottom();
  }

  getMessages(): Message[] {
    return this.messages;
  }

  addNewUserMessage(message: Message) {
    this.actnEmitter.emit(message);
  }

  addNewMessage(message: Message) {
    this.messages.push(message);
    //this.scrollToBottom();
    setTimeout(() => this.scrollToBottom(), 50);
  }

  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch (err) {
    }
  }

}
