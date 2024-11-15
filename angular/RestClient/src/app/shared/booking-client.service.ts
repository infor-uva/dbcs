import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Booking } from '../../types/Booking'; // Ajusta la ruta a tu modelo Booking

@Injectable({
  providedIn: 'root',
})
export class BookingClientService {
  private URI = environment.bookingAPI;

  constructor(private http: HttpClient) {}

  // Método para crear una nueva reserva
  createBooking(bookingRequest: Booking): Observable<Booking> {
    return this.http.post<Booking>(this.URI, bookingRequest, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
    });
  }

  // Método para obtener todas las reservas
  getAllBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(this.URI);
  }

  // Método para obtener una reserva por ID
  getBookingById(id: number): Observable<Booking> {
    return this.http.get<Booking>(`${this.URI}/${id}`);
  }

  getUserBookings(userId: number) {
    // TODO revisar tras división en microservicios
    return this.http.get<Booking[]>(`${this.URI}/${userId}/bookings`);
  }

  // Método para eliminar una reserva
  deleteBooking(id: number) {
    return this.http.delete(`${this.URI}/${id}`);
  }
}
