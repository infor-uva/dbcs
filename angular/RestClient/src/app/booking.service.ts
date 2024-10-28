// booking.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
interface BookingRequest {
  userId: number;      // ID del usuario que realiza la reserva
  hotelId: number;     // ID del hotel en el que se realiza la reserva
  roomType: string;    // Tipo de habitación (single, double, suite)
  startDate: string;   // Fecha de inicio de la reserva
  endDate: string;     // Fecha de fin de la reserva
}
import { Booking } from '../types/Booking'; // Ajusta la ruta a tu modelo Booking

@Injectable({
  providedIn: 'root' // Esto hace que el servicio esté disponible en toda la aplicación
})
export class BookingService {
  private apiUrl = 'http://localhost:8080/api/bookings'; // Cambia esta URL a la de tu API

  constructor(private http: HttpClient) {}

  // Método para crear una nueva reserva
  createBooking(bookingRequest: BookingRequest): Observable<Booking> {
    return this.http.post<Booking>(this.apiUrl, bookingRequest, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
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

  // Agrega más métodos según sea necesario
}
