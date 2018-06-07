import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FdgViewComponent } from './fdg-view.component';

describe('FdgViewComponent', () => {
  let component: FdgViewComponent;
  let fixture: ComponentFixture<FdgViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FdgViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FdgViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
