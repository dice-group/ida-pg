import { Component, OnInit } from '@angular/core';
import {RestService} from '../../service/rest/rest.service';
import {Router} from '@angular/router';
import {UserService} from '../../service/user/user.service';

@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.component.html',
  styleUrls: ['./topbar.component.css']
})
export class TopbarComponent implements OnInit {

  showButton = false;

  constructor(private userservice: UserService, private restservice: RestService, private router: Router) { }

  ngOnInit() {
    this.isAdmin();
  }

  logout() {
    this.restservice.postRequest('auth/logout', {}, {}).subscribe(resp => {
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
