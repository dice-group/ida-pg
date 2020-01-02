import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SoldierTimelineComponent } from './soldier-timeline.component';

describe('SoldierTimelineComponent', () => {
  let component: SoldierTimelineComponent;
  let fixture: ComponentFixture<SoldierTimelineComponent>;

  beforeEach(async(() => { 
    TestBed.configureTestingModule({
      declarations: [ SoldierTimelineComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SoldierTimelineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
