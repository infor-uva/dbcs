import { TestBed } from '@angular/core/testing';

import { HotelClientService } from './hotel-client.service';

describe('HotelClientService', () => {
  let service: HotelClientService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HotelClientService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
