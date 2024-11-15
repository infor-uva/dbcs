import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthClientService {
  private readonly URI = environment.authAPI;

  constructor() {}
}
