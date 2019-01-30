import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {MatSnackBar} from '@angular/material';

@Component({
  selector: 'app-speech-input',
  templateUrl: './speech-input.component.html',
  styleUrls: ['./speech-input.component.css']
})
export class SpeechInputComponent implements OnInit {

  constructor(public snackBar: MatSnackBar) { }
  public recognition;
  @Output() msgText = new EventEmitter<string>();

  ngOnInit() {
    const SpeechRecognition = (<any>window).webkitSpeechRecognition || (<any>window).SpeechRecognition;
    this.recognition = new SpeechRecognition();
  }

  speechConversion() {
    this.openSnackBar('Listening through Microphone', '');
    this.recognition.start();
    this.recognition.onresult = (event) => {
      const speechToText = event.results[0][0].transcript;
      this.msgText.emit(speechToText);
    };
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 3000,
    });
  }
}
