import { Component, OnInit } from '@angular/core';
import {UserService} from "../../service/user/user.service";
import {RestService} from "../../service/rest/rest.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-topmenu',
  templateUrl: './topmenu.component.html',
  styleUrls: ['./topmenu.component.css']
})
export class TopmenuComponent implements OnInit {

  showButton = false;
  constructor(private userservice: UserService, private restservice: RestService, private router: Router) { }

  ngOnInit() {
    this.isAdmin();
  }

  logout() {
    this.restservice.postRequest('auth/logout', {}, {}).subscribe(resp => {
      localStorage.removeItem('user');
      this.router.navigate(['login']);
    } );
  }

  isAdmin() {
    this.restservice.getRequest('auth/check-login', {}).subscribe(resp => {
      const returnResp = this.userservice.processUserResponse(resp);
      if (returnResp.status === true && returnResp.respData['isAdmin'] === true) {
        this.showButton = true;
      } else {
        this.router.navigate(['']);
      }
    });
  }

}
