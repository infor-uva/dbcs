// booking.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Booking } from '../../types/Booking'; // Ajusta la ruta a tu modelo Booking

@Injectable({
  providedIn: 'root', // Esto hace que el servicio esté disponible en toda la aplicación
})
export class BookingService {
  private apiUrl = 'http://localhost:8080/bookings';

  constructor(private http: HttpClient) {}

  // Método para crear una nueva reserva
  createBooking(bookingRequest: Booking): Observable<Booking> {
    return this.http.post<Booking>(this.apiUrl, bookingRequest, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
    });
  }

  // Método para obtener todas las reservas
  getAllBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(this.apiUrl);
  }

  // Método para obtener una reserva por ID
  getBookingById(id: number): Observable<Booking> {
    return this.http.get<Booking>(`${this.apiUrl}/${id}`);
  }

  // Método para eliminar una reserva
  deleteBooking(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
