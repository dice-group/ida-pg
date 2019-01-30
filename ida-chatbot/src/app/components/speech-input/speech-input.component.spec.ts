import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SpeechInputComponent } from './speech-input.component';

describe('SpeechInputComponent', () => {
  let component: SpeechInputComponent;
  let fixture: ComponentFixture<SpeechInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SpeechInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SpeechInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
