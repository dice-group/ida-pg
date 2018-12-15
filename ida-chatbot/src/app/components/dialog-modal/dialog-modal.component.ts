import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError} from 'rxjs/operators';

@Component({
  selector: 'app-dialog-modal',
  templateUrl: './dialog-modal.component.html',
  styleUrls: ['./dialog-modal.component.css']
})
export class DialogModalComponent implements OnInit {

  fileName = 'Select CSV or XML file...';
  file;
  fileSelected = false;

  constructor(private _http: HttpClient) {
  }

  ngOnInit() {
  }

  fileManager(event) {
    if (event.target.files.length > 0) {
      this.fileSelected = true;
      this.file = event.target.files[0];
      this.fileName = this.file.name;
    }
  }

  uploadDataset() {
    console.log('jklkjhjkl');
    const formData = new FormData();
    formData.append('file', this.file);
    formData.append('fileName', this.fileName);
    this._http.post('http://localhost:3000/ida-udf-ws/adapter/xml/file', formData).subscribe(
      (res) => {
        console.log(res);
      },
      err => console.log(err)
    );
  }

}
