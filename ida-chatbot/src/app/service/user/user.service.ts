import { Injectable } from '@angular/core';
import {HttpClient, HttpClientModule, HttpHeaders} from '@angular/common/http';
import {ResponseBean} from '../../models/response-bean';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

    processUserResponse(resp: ResponseBean) {
      let responseStatus = {
        status : false,
        respMsg: '',
        respData: []
      };
      if (resp.errCode === 15) {  //  FAILURE_USERLIST
        responseStatus = {
          status: false,
          respMsg: 'Unexpected error has occurred while fetching the data',
          respData: [],
        };
      } else if (resp.errCode === 16) { //  FAILURE_UPDATEUSER
        responseStatus = {
          status: false,
          respMsg: 'User no longer exists',
          respData: [],
        };
      } else if (resp.errCode === 19) { //  FAILURE_USEREXISTS
        responseStatus = {
          status: false,
          respMsg: 'User already exists for this username',
          respData: [],
        };
      } else if (resp.errCode === 20) { //  FAILURE_CREDENTIALSINCORRECT
          responseStatus = {
            status: false,
            respMsg: 'Wrong username or password',
            respData: [],
          };
      } else if (resp.errCode === 21) { //  LOGIN_REQUIRED
        responseStatus = {
          status: false,
          respMsg: '',
          respData: []
        };
      } else if (resp.errCode === 23) { //  ALREADY_LOGGEDIN
        responseStatus = {
          status: true,
          respMsg: '',
          respData: resp.payload
        };
      } else if (resp.errMsg === 'Login Successful') {
        responseStatus = {
          status: true,
          respMsg: '',
          respData: []
        };
      } else {
        responseStatus = {
          status: true,
          respMsg: '',
          respData: resp.payload.users
        };
      }
      return responseStatus;
    }

}
