import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DatasetUploadModalComponent } from './dataset-upload-modal.component';

describe('DatasetUploadModalComponent', () => {
  let component: DatasetUploadModalComponent;
  let fixture: ComponentFixture<DatasetUploadModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DatasetUploadModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatasetUploadModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
