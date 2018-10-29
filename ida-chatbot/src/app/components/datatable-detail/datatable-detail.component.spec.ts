import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DatatableDetailComponent } from './datatable-detail.component';

describe('DatatableDetailComponent', () => {
  let component: DatatableDetailComponent;
  let fixture: ComponentFixture<DatatableDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DatatableDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatatableDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
