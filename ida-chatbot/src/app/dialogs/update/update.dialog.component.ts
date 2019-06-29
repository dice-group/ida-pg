import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {Component, Inject, OnInit} from '@angular/core';
import {UserService} from '../../service/user/user.service';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MatSnackBar} from '@angular/material';
import {RestService} from "../../service/rest/rest.service";

@Component({
    selector: 'app-update.dialog',
    templateUrl: './update.dialog.html',
    styleUrls: ['./update.dialog.css']
})
export class UpdateDialogComponent implements  OnInit {

  public updateForm: FormGroup;

    constructor(public dialogRef: MatDialogRef<UpdateDialogComponent>, public snackBar: MatSnackBar,
                @Inject(MAT_DIALOG_DATA) public data: any, public userservice: UserService , private restservice: RestService) { }

  ngOnInit() {
    this.updateForm = new FormGroup({
      // id: new FormControl({disabled: true}, Validators.required),
      // id: new FormControl('', [Validators.required]),
      username: new FormControl('', [Validators.required, Validators.email, Validators.maxLength(60)]),
      firstname: new FormControl('', [Validators.required, Validators.maxLength(60)]),
      lastname: new FormControl('', [Validators.required, Validators.maxLength(60)]),
    });

    this.updateForm.setValue({
      username: this.data.username,
      firstname: this.data.firstname,
      lastname: this.data.lastname,
    });

    this.updateForm.get('username').disable();
  }

    hasError(controlName: string, errorName: string) {
      return this.updateForm.controls[controlName].hasError(errorName);
    }

    submit(updateFormValue) {
      const user = {
        username: this.data.username,
        firstname: updateFormValue.firstname,
        lastname: updateFormValue.lastname
      }
      this.restservice.postRequest('/user/update', user, {}).subscribe(resp => {
        const returnResp = this.userservice.processUserResponse(resp);
        if (returnResp.status === false) {
          this.snackBar.open(returnResp.respMsg, '', {
            duration: 4000,
          });
        } else {
          this.snackBar.open('User updated successfully', '', {
            duration: 3000,
          });
        }
      });
    }

    onNoClick(): void {
        this.dialogRef.close();
    }

}
