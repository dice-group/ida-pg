import { TestBed, inject } from '@angular/core/testing';

import { UniqueIdProviderService } from './unique-id-provider.service';

describe('UniqueIdProviderService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UniqueIdProviderService]
    });
  });

  it('should be created', inject([UniqueIdProviderService], (service: UniqueIdProviderService) => {
    expect(service).toBeTruthy();
  }));
});
