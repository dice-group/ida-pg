import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VennViewComponent } from './venn-view.component';

describe('VennViewComponent', () => {
  let component: VennViewComponent;
  let fixture: ComponentFixture<VennViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VennViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VennViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
