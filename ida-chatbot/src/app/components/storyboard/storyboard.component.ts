import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {RestService} from '../../service/rest/rest.service';
import {AppComponent} from '../../app.component';

@Component({
  selector: 'app-storyboard',
  templateUrl: './storyboard.component.html',
  styleUrls: ['./storyboard.component.css']
})
export class StoryboardComponent implements OnInit {

  storyUID: string;
  constructor(private route: ActivatedRoute, private restservice: RestService, private appcomponent: AppComponent) {
    this.route.queryParams.subscribe(params => {
      this.storyUID = params['id'];
    });
  }

  ngOnInit() {
    this.callbackendAPI();
  }

  callbackendAPI() {
    const prmobj = {};
    this.restservice.getRequest('/getstory?id=' + this.storyUID, prmobj).subscribe(resp => this.appcomponent.processBotResponse(resp));
  }

}
