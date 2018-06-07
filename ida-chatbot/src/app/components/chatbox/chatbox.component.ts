import {AfterViewChecked, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Message} from '../../models/message';

@Component({
  selector: 'app-chatbox',
  templateUrl: './chatbox.component.html',
  styleUrls: ['./chatbox.component.css']
})
export class ChatboxComponent implements OnInit, AfterViewChecked  {
  @ViewChild('scrollMe') private myScrollContainer: ElementRef;
  curDate = new Date();
  msg1: Message = new Message('Hello there! How can I help you?', 'Assistant' , 'chatbot' , this.curDate );
  msg2: Message = new Message('Hi! Could you please load the City dataset.', 'User' , 'user' , this.curDate );
  msg3: Message = new Message('Yes I can, Please wait a moment.', 'Assistant' , 'chatbot' , this.curDate );
  msg4: Message = new Message('ok', 'Assistant' , 'User' , this.curDate );
  msg5: Message = new Message('City dataset is loaded.', 'Assistant' , 'chatbot' , this.curDate );
  msg6: Message = new Message('Thank you!', 'User' , 'user' , this.curDate );
  messages: Message[] = [this.msg1, this.msg2, this.msg3, this.msg4, this.msg5, this.msg6];
  constructor() { }

  ngOnInit() {
    this.scrollToBottom();
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  getMessages(): Message[] {
    return this.messages;
  }
  addNewMessage(message: Message) {
    this.messages.push(message);
  }

  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch (err) { }
  }

}
