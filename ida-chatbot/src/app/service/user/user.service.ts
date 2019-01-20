import { Injectable } from '@angular/core';
import {Http, RequestOptions, Headers, HttpModule} from '@angular/http';
import {HttpClient, HttpClientModule, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import {ResponseBean} from "../../models/response-bean";
// import 'rxjs/add/operator/map';
// import {map} from "rxjs/operators";
// import {ResponseBean} from "../../../../ida-chatbot/src/app/models/response-bean";
// import {Message} from "../../../../ida-chatbot/src/app/models/message";

let baseURL : string = 'http://localhost:8080/';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

    processUserResponse(resp: ResponseBean) {
      if (resp.payload !== null) {
        return resp.payload.users;
      }
    }

}
