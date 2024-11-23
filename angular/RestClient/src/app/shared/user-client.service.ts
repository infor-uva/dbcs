import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { User, UserState } from '../../types';

@Injectable({
  providedIn: 'root',
})
export class UserClientService {
  private readonly URI = environment.userAPI;
  constructor(private http: HttpClient) {}

  getUser(userId: number) {
    return this.http.get<User>(`${this.URI}/${userId}`);
  }

  getAllUsers() {
    return this.http.get<User[]>(this.URI, {
      observe: 'body',
    });
  }

  alterUserStatus(userId: number, status: UserState) {
    return this.http.patch(
      `${this.URI}/${userId}`,
      {
        status,
      },
      {
        observe: 'response',
        responseType: 'text',
      }
    );
  }
}
