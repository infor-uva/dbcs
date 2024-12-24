import { TestBed } from '@angular/core/testing';

import { AuthClientService } from './auth-client.service';
import { Session } from '@core/models';

describe('AuthClientService', () => {
  let service: AuthClientService;
  let s: Session;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthClientService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
