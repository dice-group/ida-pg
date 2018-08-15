import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeckglHexViewComponent } from './deckgl-hex-view.component';

describe('DeckglHexViewComponent', () => {
  let component: DeckglHexViewComponent;
  let fixture: ComponentFixture<DeckglHexViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeckglHexViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeckglHexViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
