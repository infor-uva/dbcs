import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Hotel, Room } from '../types';
import { SessionService } from './session.service';
import { catchError, map, switchMap, throwError } from 'rxjs';
import { log } from 'console';

@Injectable({
  providedIn: 'root',
})
export class HotelClientService {
  private readonly URI = environment.hotelAPI;
  constructor(
    private http: HttpClient,
    private sessionService: SessionService
  ) {}

  getHotel(id: number) {
    const url = `${this.URI}/${id}`;
    return this.http.get<Hotel>(url);
  }

  getAllHotels() {
    const url = `${this.URI}`;
    return this.http.get<Hotel[]>(url);
  }

  deleteHotel(id: number) {
    const url = `${this.URI}/${id}`;
    return this.http.delete(url);
  }

  addHotel(hotel: Hotel) {
    const url = `${this.URI}`;
    return this.sessionService.getSession().pipe(
      map((session) => {
        if (!session) {
          throw new Error('No session found');
        }
        const { id } = session;
        const hotelWithHM = { ...hotel, hotelManager: { id } };
        return hotelWithHM;
      }),
      switchMap((hotelWithHM) =>
        this.http.post(url, hotelWithHM).pipe(
          // Opcional: Puedes manejar transformaciones o errores aquí.
          catchError((err) => {
            console.error('Error al agregar hotel:', err);
            return throwError(() => err);
          })
        )
      )
    );
  }

  alterRoomAvailability(
    hotelId: number,
    roomId: number,
    availability: boolean
  ) {
    const url = `${this.URI}/${hotelId}/rooms/${roomId}`;
    return this.http.patch(url, { available: availability });
  }

  getRoomsAvailableInDateRange(hotelId: number, start: Date, end: Date) {
    const startStr = start.toISOString().split('T')[0];
    const endStr = end.toISOString().split('T')[0];
    const url = `${this.URI}/${hotelId}/rooms?start=${startStr}&end=${endStr}`;
    return this.http.get<Room[]>(url);
  }
}
