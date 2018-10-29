import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SsbViewComponent } from './ssb-view.component';

describe('SsbViewComponent', () => {
  let component: SsbViewComponent;
  let fixture: ComponentFixture<SsbViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SsbViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SsbViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
