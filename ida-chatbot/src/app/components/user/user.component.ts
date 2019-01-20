import { Component, OnInit } from '@angular/core';
import {UserService} from '../../service/user/user.service';
// import {User} from '../../interfaces/user';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material';
import {UpdateDialogComponent} from '../../dialogs/update/update.dialog.component';
import {DeleteDialogComponent} from '../../dialogs/delete/delete.dialog.component';
import {RestService} from '../../service/rest/rest.service';
import {ResponseBean} from '../../models/response-bean';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

    index: number;
    id: number;

    displayedColumns: string[] = ['id', 'username', 'firstname', 'lastname', 'actions'];
    dataSource = [];
    showSpinner = false;

  constructor(private restservice: RestService, private userservice: UserService, private router: Router, public dialog: MatDialog) {}

  ngOnInit() {
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

    loadUser() {
      this.showSpinner = true;
      this.restservice.getRequest('user/list', {}).subscribe(resp => {
        this.dataSource = this.userservice.processUserResponse(resp);
      } );
        this.showSpinner = false;
    }

}
