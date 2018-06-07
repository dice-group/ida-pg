import { Component } from '@angular/core';
import {Message} from './models/message';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';
  AppComponent() {
    /*this.messages.push(msg1, msg2, msg3, msg4, msg5, msg6);*/
  }
}
