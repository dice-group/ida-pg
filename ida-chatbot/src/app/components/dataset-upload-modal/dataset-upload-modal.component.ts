import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError} from 'rxjs/operators';
import {MatDialogRef, MatSnackBar} from '@angular/material';

@Component({
  selector: 'app-dialog-modal',
  templateUrl: './dataset-upload-modal.component.html',
  styleUrls: ['./dataset-upload-modal.component.css']
})
export class DatasetUploadModalComponent implements OnInit {

  fileName = 'Select CSV or XML file...';
  file;
  fileSelected = false;
  showProgress = false;

  constructor(private _http: HttpClient, public dialogRef: MatDialogRef<DatasetUploadModalComponent>, public snackBar: MatSnackBar) {}

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
    this.showProgress = true;
    const formData = new FormData();
    formData.append('file', this.file);
    formData.append('fileName', this.fileName);
    this._http.post('http://localhost:3000/ida-udf-ws/adapter/xml/file', formData).subscribe(
      (res) => {
        this.dialogRef.close();
        this.snackBar.open('Dataset uploaded successfully', 'Close', {
          duration: 3000,
        });
      },
      (err) => {
        this.snackBar.open('Dataset upload failed! Please try again', 'Close', {
          duration: 3000,
        });
        this.showProgress = false;
      }
    );
  }

}
