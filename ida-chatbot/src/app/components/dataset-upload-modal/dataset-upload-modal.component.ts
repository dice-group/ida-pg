import {Component, Inject, Input, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {MAT_DIALOG_DATA, MatDialogRef, MatSnackBar} from '@angular/material';

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

  constructor(@Inject(MAT_DIALOG_DATA) public data,
              private _http: HttpClient,
              public dialogRef: MatDialogRef<DatasetUploadModalComponent>,
              public snackBar: MatSnackBar) {}

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
    console.log(this.data.datasetName);
    this.showProgress = true;
    const formData = new FormData();
    formData.append('file', this.file);
    formData.append('fileName', this.data.datasetName);
    this._http.post('http://localhost:8080/ida-ws/message/file', formData).subscribe(
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
