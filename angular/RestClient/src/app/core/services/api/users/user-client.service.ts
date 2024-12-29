import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../../../environments/environment';
import { SessionService } from '../../session/session.service';
import { tap } from 'rxjs';
import { Client, ClientState, User } from '@features/users';
import { Session } from '@core/models';

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
  alterUserStatus(userId: number, status: ClientState) {
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
        this.sessionService.updateData({
          id: userId,
          ...user,
        } as Partial<Session>);
      })
    );
  }
}
