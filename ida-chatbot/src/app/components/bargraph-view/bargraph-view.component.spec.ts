import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BargraphViewComponent } from './bargraph-view.component';

describe('BargraphViewComponent', () => {
  let component: BargraphViewComponent;
  let fixture: ComponentFixture<BargraphViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BargraphViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BargraphViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
