import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {UserService} from '../../service/user/user.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {RestService} from '../../service/rest/rest.service';
import {MatSnackBar} from '@angular/material';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

    public loginForm: FormGroup;

    showSpinner = false;

    constructor(private restservice: RestService, private userservice: UserService, private router: Router , public snackBar: MatSnackBar) { }

  ngOnInit() {
    this.checkLoggedIn();
      this.loginForm = new FormGroup({
          username: new FormControl('', [Validators.required, Validators.email]),
          password: new FormControl('', [Validators.required]),
      });
  }

    public hasError = (controlName: string, errorName: string) => {
        return this.loginForm.controls[controlName].hasError(errorName);
    }

    public loginUser = (loginFormValue) => {
        if (this.loginForm.valid) {
            this.login(loginFormValue);
        }
    }

    checkLoggedIn() {
      this.restservice.getRequest('auth/check-login', {}).subscribe(resp => {
        const returnResp = this.userservice.processUserResponse(resp);
        if (returnResp.status === true) {
          this.router.navigate(['']);
        }
      });
    }

    login(loginFormValue) {
      const userData = {username: loginFormValue.username , password : loginFormValue.password};

      this.showSpinner = true;
      this.restservice.postRequest('auth/login-action', userData, {}).subscribe(resp => {
        const returnResp = this.userservice.processUserResponse(resp);
        this.showSpinner = false;
        if (returnResp.status === false) {
          this.snackBar.open(returnResp.respMsg, '', {
            duration: 4000,
          });
        } else {
          this.snackBar.open('Login successful', '', {
            duration: 3000,
          });
          this.router.navigate(['']);
        }
      });

    }

}
