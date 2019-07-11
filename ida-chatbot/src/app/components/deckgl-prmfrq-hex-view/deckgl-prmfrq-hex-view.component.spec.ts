import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeckglPrmfrqHexViewComponent } from './deckgl-prmfrq-hex-view.component';

describe('DeckglPrmfrqHexViewComponent', () => {
  let component: DeckglPrmfrqHexViewComponent;
  let fixture: ComponentFixture<DeckglPrmfrqHexViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeckglPrmfrqHexViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeckglPrmfrqHexViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
