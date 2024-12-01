import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Client, Session, User, UserState } from '../types';
import { SessionService } from './session.service';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserClientService {
  private readonly URI = environment.userAPI;

  constructor(
    private http: HttpClient,
    private sessionService: SessionService
  ) {}

  // Obtener un usuario por ID
  getUser(userId: number) {
    return this.http.get<User>(`${this.URI}/${userId}`);
  }

  // Obtener todos los usuarios
  getAllUsers() {
    return this.http.get<Client[]>(this.URI, {
      observe: 'body',
    });
  }

  // Cambiar estado de un usuario
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

  // Actualizar los datos del usuario
  updateUser(userId: number, user: Partial<User>) {
    return this.http.put(`${this.URI}/${userId}`, user).pipe(
      tap(() => {
        this.sessionService.updateData(user as Partial<Session>);
      })
    );
  }

  // Cambiar la contraseña del usuario
  updatePassword(currentPassword: string, newPassword: string) {
    return this.http.patch(
      `${this.URI}/me/password`,
      { currentPassword, newPassword },
      {
        observe: 'response',
        responseType: 'text',
      }
    );
  }
}
