import {Component, Input, OnInit} from '@angular/core';
import {Message} from '../../models/message';

@Component({
  selector: 'app-message-list',
  templateUrl: './message-list.component.html',
  styleUrls: ['./message-list.component.css']
})
export class MessageListComponent implements OnInit {
  @Input('messages')
  public messages: Message[];
  curDate = new Date();
  constructor() { }

  ngOnInit() {
    /*const msg1: Message = new Message('Hello there! How can I help you?', 'Assistant' , 'chatbot' , this.curDate );
    const msg2: Message = new Message('Hi! Could you please load the City dataset.', 'User' , 'user' , this.curDate );
    const msg3: Message = new Message('Yes I can, Please wait a moment.', 'Assistant' , 'chatbot' , this.curDate );
    const msg4: Message = new Message('ok', 'Assistant' , 'User' , this.curDate );
    const msg5: Message = new Message('City dataset is loaded.', 'Assistant' , 'chatbot' , this.curDate );
    const msg6: Message = new Message('Thank you!', 'User' , 'user' , this.curDate );

    this.messages.push(msg1, msg2, msg3, msg4, msg5, msg6);*/
    //console.log(this.messages);
  }

}
