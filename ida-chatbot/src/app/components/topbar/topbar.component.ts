import {Component, Input, OnInit} from '@angular/core';
import {RestService} from '../../service/rest/rest.service';
import {Router} from '@angular/router';
import {UserService} from '../../service/user/user.service';

@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.component.html',
  styleUrls: ['./topbar.component.css']
})
export class TopbarComponent implements OnInit {
  constructor() { }

  ngOnInit() {}
}
