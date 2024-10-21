import { TestBed } from '@angular/core/testing';

import { ClienteApiRestService } from './cliente-api-rest.service';

describe('ClienteApiRestService', () => {
  let service: ClienteApiRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClienteApiRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
