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
    return this.http.post(`${this.URI}/login`, { email, password });
  }

  register(name: String, email: String, password: String, rol?: String) {
    return this.http.post(`${this.URI}/register`, {
      name,
      email,
      password,
      rol,
    });
  }

  changePassword(data: {
    email: String;
    password: String;
    newPassword: String;
  }) {
    console.log(data);
    return this.http.post(`${this.URI}/password`, data);
  }

  deleteUser(userId: number, password: String) {
    return this.http.post(`${this.URI}/delete/${userId}`, { password });
  }
}
