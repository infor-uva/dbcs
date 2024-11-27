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

  // Obtener un usuario por ID
  getUser(userId: number) {
    return this.http.get<User>(`${this.URI}/${userId}`);
  }

  // Obtener todos los usuarios
  getAllUsers() {
    return this.http.get<User[]>(this.URI, {
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

  // Obtener el usuario actual (autenticado)
  getCurrentUser() {
    return this.http.get<User>(`${this.URI}/me`);
  }

  // Actualizar los datos del usuario
  updateUser(user: Partial<User>) {
    return this.http.patch(`${this.URI}/me`, user, {
      observe: 'response',
      responseType: 'text',
    });
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
