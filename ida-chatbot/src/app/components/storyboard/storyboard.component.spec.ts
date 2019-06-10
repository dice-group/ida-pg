import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StoryboardComponent } from './storyboard.component';

describe('StoryboardComponent', () => {
  let component: StoryboardComponent;
  let fixture: ComponentFixture<StoryboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StoryboardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StoryboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
