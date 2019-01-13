import { Injectable } from '@angular/core';
import {Http, RequestOptions, Headers} from '@angular/http';
import {Observable} from 'rxjs/Observable';

let url : string = 'http://localhost:8080/api/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http : Http) { }

    findAll() {
        return this
            .http
            .get(url)
            .map(res => res.json())
            .catch(this.handleError);
    }

    save(user) {
        const headers = new Headers({'Content-Type': 'application/json', 'Cache-Control': 'no-cache'});
        const options = new RequestOptions({headers: headers});
        return this
            .http
            .post(url, user, options)
            .map(res => res.json())
            .catch(this.handleError);
    }

    detele(id) {
        return this
            .http
            .delete(url + '/' + id)
            .map(res => res.json())
            .catch(this.handleError);
    }

    handleError(error) {
        return Observable.throw(error.json().error || 'Server error');
    }
}
