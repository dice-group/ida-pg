import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DatatableViewComponent } from './datatable-view.component';

describe('DatatableViewComponent', () => {
  let component: DatatableViewComponent;
  let fixture: ComponentFixture<DatatableViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DatatableViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatatableViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
