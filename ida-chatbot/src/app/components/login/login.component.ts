import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {UserService} from '../../service/user/user.service';
// import {User} from '../../interfaces/user';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {RestService} from '../../service/rest/rest.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

    public loginForm: FormGroup;

    errMsg = '';
    showSpinner = false;

    constructor(private restservice: RestService,private userService: UserService, private router: Router) { }

  ngOnInit() {
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

    login(loginFormValue) {
        const user = {username: loginFormValue.username , password : loginFormValue.loginFormValue};

        this.showSpinner = true;

      this.restservice.postRequest('login', {user: user}, {}).subscribe(resp => {
        // this.dataSource = this.userservice.processUserResponse(resp);

        this.showSpinner = false;
        this.router.navigate(['']);
      } );

        // this.userService
        //     .callAPI('post', 'user/new', user)
        //     .subscribe(resp => {
        //         console.log(resp);
        //         this.showSpinner = false;
        //         // this.router.navigate(['login']);
        //         // this.snackBar.open('Signup successful', '', {
        //         //     duration: 2000,
        //         // });
        //
        //         // if(resp.payload.id > 0) {
        //         //     this
        //         //     .router
        //         //     .navigate(['login']);
        //         // } else {
        //         //     this.errMsg = resp.errMsg;
        //         // }
        //         this.showSpinner = false;
        //
        //     }, error => {
        //         console.log(error);
        //     });
    }

}
