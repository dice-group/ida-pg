import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {UserService} from '../../service/user/user.service';
// import {User} from '../../interfaces/user';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {MatSnackBar} from '@angular/material';
import {RestService} from '../../service/rest/rest.service';


@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

    public signupForm: FormGroup;

    errMsg = '';
    showSpinner = false;

    constructor(private restservice: RestService, private userservice: UserService, private router: Router, public snackBar: MatSnackBar) { }

    ngOnInit() {
      this.checkLoggedIn();
        this.signupForm = new FormGroup({
            username: new FormControl('', [Validators.required, Validators.email, Validators.maxLength(60)]),
            firstname: new FormControl('', [Validators.required, Validators.maxLength(60)]),
            lastname: new FormControl('', [Validators.required, Validators.maxLength(60)]),
            password: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(6)]),
        });
    }

    public hasError = (controlName: string, errorName: string) => {
        return this.signupForm.controls[controlName].hasError(errorName);
    }

    public createUser = (signupFormValue) => {
        if (this.signupForm.valid) {
            this.signup(signupFormValue);
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

    signup(signupFormValue) {
        this.errMsg = '';
        this.showSpinner = true;

        const user = {
            username: signupFormValue.username,
            firstname: signupFormValue.firstname,
            lastname: signupFormValue.lastname,
            password: signupFormValue.password
        }

      this.restservice.postRequest('user/new', user, {}).subscribe(resp => {
        const returnResp = this.userservice.processUserResponse(resp);
        this.showSpinner = false;
        if (returnResp.status === false) {
          this.snackBar.open(returnResp.respMsg, '', {
            duration: 4000,
          });
        } else {
          this.snackBar.open('Signup successful', '', {
            duration: 3000,
          });
          this.router.navigate(['login']);
        }
      });
    }
}
