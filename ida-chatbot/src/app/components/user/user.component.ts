import { Component, OnInit } from '@angular/core';
import {UserService} from '../../service/user/user.service';
import {Router} from '@angular/router';
import {MatDialog, MatSnackBar, MatTableDataSource} from '@angular/material';
import {UpdateDialogComponent} from '../../dialogs/update/update.dialog.component';
import {DeleteDialogComponent} from '../../dialogs/delete/delete.dialog.component';
import {RestService} from '../../service/rest/rest.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

    index: number;
    id: number;
    isHidden = false;

    displayedColumns: string[] = ['id', 'username', 'firstname', 'lastname', 'actions'];
    // dataSource = [];
    dataSource = new MatTableDataSource();
    showSpinner = false;

  constructor(private restservice: RestService, private userservice: UserService, private router: Router, public dialog: MatDialog , public snackBar: MatSnackBar) {}

  ngOnInit() {
      this.isAdmin();
      this.loadUser();
  }

    startEdit(i: number, id: number, username: string, firstname: string, lastname: string) {
        this.id = id;
        this.index = i;

        const dialogRef = this.dialog.open(UpdateDialogComponent, {
            data: {id: id, username: username, firstname: firstname, lastname: lastname}
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === 1) {
              this.loadUser();
            }
        });
    }

    deleteItem(i: number, id: number, username: string) {
        this.index = i;
        this.id = id;
        const dialogRef = this.dialog.open(DeleteDialogComponent, {
            data: {id: id, username: username}
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === 1) {
              this.loadUser();
            }
        });
    }

    isAdmin() {
      this.restservice.getRequest('auth/check-login', {}).subscribe(resp => {
        const returnResp = this.userservice.processUserResponse(resp);
        if (returnResp.status === true && returnResp.respData['isAdmin'] === true) {
          this.isHidden = false;
        } else {
          this.router.navigate(['']);
        }
      });
    }

    loadUser() {
      this.showSpinner = true;
      this.restservice.getRequest('user/list', {}).subscribe(resp => {
        const returnResp = this.userservice.processUserResponse(resp);
        this.showSpinner = false;
        if (returnResp.status === false) {
          this.snackBar.open(returnResp.respMsg, '', {
            duration: 4000,
          });
        } else {
          this.dataSource = new MatTableDataSource(returnResp.respData);
        }
      });
    }

    applyFilter(filterValue: string) {
      this.dataSource.filter = filterValue.trim().toLowerCase();
    }

}
