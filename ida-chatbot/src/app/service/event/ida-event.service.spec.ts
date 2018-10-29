import { TestBed, inject } from '@angular/core/testing';

import { IdaEventService } from './ida-event.service';

describe('IdaEventService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [IdaEventService]
    });
  });

  it('should be created', inject([IdaEventService], (service: IdaEventService) => {
    expect(service).toBeTruthy();
  }));
});
