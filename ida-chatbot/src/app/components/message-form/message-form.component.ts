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
  public recognition ;
  ngOnInit() {
    this.restservice.requestEvnt.subscribe(val => { this.toggleProgressBar(val); });
	(<any>window).SpeechRecognition = (<any>window).webkitSpeechRecognition || (<any>window).SpeechRecognition;
	this.recognition = new SpeechRecognition();
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

  keyDownFunction(event,msg: string) {
    if (event.keyCode === 13) {
      this.sendMessage(msg);
    }
  }
	
  speechConversion(chatmsg) {
  	this.recognition.start();
  	this.recognition.onresult = (event) => {
    	const speechToText = event.results[0][0].transcript;     
    	chatmsg.value = speechToText;
  	}
  }
}
