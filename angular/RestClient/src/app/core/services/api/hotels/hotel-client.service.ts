import { Injectable } from '@angular/core';
import { environment } from '../../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { SessionService } from '../../session/session.service';
import { catchError, map, switchMap, throwError } from 'rxjs';
import { Hotel, Room } from '@features/hotels';


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

  getAllHotels(startDate?: Date, endDate?: Date) {
    const url = `${this.URI}`;
    if (!startDate || !endDate) return this.http.get<Hotel[]>(url);
    const start = new Date(startDate).toISOString().split('T')[0];
    const end = new Date(endDate).toISOString().split('T')[0];
    return this.http.get<Hotel[]>(url, { params: { start, end } });
  }

  getAllHotelsByUser(userId: number, startDate?: Date, endDate?: Date) {
    const url = `${this.URI}`;
    if (!startDate || !endDate)
      return this.http.get<Hotel[]>(url, { params: { managerId: userId } });
    const start = new Date(startDate).toISOString().split('T')[0];
    const end = new Date(endDate).toISOString().split('T')[0];
    return this.http.get<Hotel[]>(url, {
      params: { managerId: userId, start, end },
    });
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
