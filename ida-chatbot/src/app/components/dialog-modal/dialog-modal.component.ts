import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-dialog-modal',
  templateUrl: './dialog-modal.component.html',
  styleUrls: ['./dialog-modal.component.css']
})
export class DialogModalComponent implements OnInit {

  fileName = 'Select CSV or XML file...';

  constructor() { }

  ngOnInit() {
  }

  fileManager (event) {
    this.fileName = event.target.files[0].name;
  }

}
