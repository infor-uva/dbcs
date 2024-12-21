import { Injectable } from '@angular/core';
import { environment } from '../../../../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AuthClientService {
  private readonly URI = environment.authAPI;

  constructor(private http: HttpClient) {}

  login(email: String, password: String) {
    return this.http.post(
      `${this.URI}/login`,
      { email, password }
      // {
      //   headers: {
      //     'Content-Type': 'application/json',
      //     'Access-Control-Allow-Origin': '*',
      //     'Access-Control-Allow-Methods':
      //       'GET, POST, OPTIONS, PUT, PATCH, DELETE',
      //     'Access-Control-Allow-Headers': 'X-Requested-With,content-type',
      //     'Access-Control-Allow-Credentials': 'true',
      //   },
      // }
    );
  }

  register(name: String, email: String, password: String, rol?: String) {
    return this.http.post(
      `${this.URI}/register`,
      {
        name,
        email,
        password,
        rol,
      }
      // {
      //   headers: {
      //     'Content-Type': 'application/json',
      //   },
      // }
    );
  }
}
