import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RdfOntologyViewComponent } from './rdf-ontology-view.component';

describe('RdfOntologyViewComponent', () => {
  let component: RdfOntologyViewComponent;
  let fixture: ComponentFixture<RdfOntologyViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RdfOntologyViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RdfOntologyViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
