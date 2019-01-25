import {MAT_DIALOG_DATA, MatDialogRef, MatSnackBar} from '@angular/material';
import {Component, Inject} from '@angular/core';
import {UserService} from '../../service/user/user.service';
import {Router} from "@angular/router";
import {RestService} from "../../service/rest/rest.service";


@Component({
    selector: 'app-delete.dialog',
    templateUrl: './delete.dialog.html',
    styleUrls: ['./delete.dialog.css']
})
export class DeleteDialogComponent {

    constructor(public dialogRef: MatDialogRef<DeleteDialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any, public userservice: UserService,
                public snackBar: MatSnackBar, private restservice: RestService) { }

    onNoClick(): void {
        this.dialogRef.close();
    }

    confirmDelete(): void {
      this.restservice.deleteRequest('user/delete/' + this.data.id).subscribe(resp => {
        this.snackBar.open('User deleted successfully', '', {
          duration: 3000,
        });

      } );
    }
}
