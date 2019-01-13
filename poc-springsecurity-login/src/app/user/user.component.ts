import { Component, OnInit } from '@angular/core';
import {UserService} from "../services/candidate.service";
import {User} from "../../interfaces/user";
import {Router} from "@angular/router";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

    user: User[];

  constructor(private userService: UserService, private router: Router) {
      // this.user = new User();
  }

  ngOnInit() {
      this.loadUser();
  }

  loadUser() {
    this
        .userService
        .findAll()
        .subscribe(data => {
            console.log(data);
            this.user = data;
        }, error => {
            console.log(error);
        });
  }

  saveUser() {
      this
        .userService
        .save(this.user)
        .subscribe(data => {
            console.log(data);
            this
                .router
                .navigate(["user"]);
        }, error => {
            this
                .loaderService
                .display(false);
            console.log(error);
        });
  }

}
