import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataViewContainerComponent } from './data-view-container.component';

describe('DataViewContainerComponent', () => {
  let component: DataViewContainerComponent;
  let fixture: ComponentFixture<DataViewContainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataViewContainerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataViewContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
