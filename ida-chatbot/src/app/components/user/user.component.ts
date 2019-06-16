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

    isHidden = true;

    displayedColumns: string[] = ['id', 'username', 'firstname', 'lastname', 'actions'];
    dataSource = new MatTableDataSource();
    showSpinner = false;

  constructor(private restservice: RestService, private userservice: UserService, private router: Router, public dialog: MatDialog , public snackBar: MatSnackBar) {}

  ngOnInit() {
      this.isAdmin();
  }

    startEdit(username: string, firstname: string, lastname: string) {

        const dialogRef = this.dialog.open(UpdateDialogComponent, {
            data: {username: username, firstname: firstname, lastname: lastname}
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === 1) {
              this.loadUser();
            }
        });
    }

    deleteItem(username: string) {
        const dialogRef = this.dialog.open(DeleteDialogComponent, {
            data: {username: username}
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === 1) {
              this.loadUser();
            }
        });
    }

    isAdmin() {
      this.restservice.getRequest('/auth/check-login', {}).subscribe(resp => {
        const returnResp = this.userservice.processUserResponse(resp);
        if (returnResp.status === true && returnResp.respData['isAdmin'] === true) {
          this.isHidden = false;
          this.loadUser();
        } else {
          this.router.navigate(['']);
        }
      });
    }

    loadUser() {
      this.showSpinner = true;
      this.restservice.getRequest('/user/list', {}).subscribe(resp => {
        const returnResp = this.userservice.processUserResponse(resp);
        this.showSpinner = false;
        let userArray= JSON.parse(resp.payload.users).results.bindings;
        let users = [];
        let user = {
          'firstname':'',
          'lastname':'',
          'username':'',
          'userrole':'',
          'password':'',
        };
        let counter = 0;
        for(let i=0; i<userArray.length; i++){
          if(counter < 5){
            user[userArray[i].predicate.value.split("#")[1]] = userArray[i].object.value;
            counter++;
          }
          if(counter == 5){
            counter = 0;
            if(user.userrole == "USER"){
              users.push(user);
            }
            user = {
              'firstname':'',
              'lastname':'',
              'username':'',
              'userrole':'',
              'password':'',
            };
          }
        }
        if (returnResp.status === false) {
          this.snackBar.open(returnResp.respMsg, '', {
            duration: 4000,
          });
        } else {
          this.dataSource = new MatTableDataSource(users);
        }
      });
    }

    applyFilter(filterValue: string) {
      this.dataSource.filter = filterValue.trim().toLowerCase();
    }

}
