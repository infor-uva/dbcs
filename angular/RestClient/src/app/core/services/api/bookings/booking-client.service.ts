import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { Booking } from '../../../models/Booking.interface'; // Ajusta la ruta a tu modelo Booking

@Injectable({
  providedIn: 'root',
})
export class BookingClientService {
  private URI = environment.bookingAPI;

  constructor(private http: HttpClient) {}

  // Método para crear una nueva reserva
  createBooking(bookingRequest: Booking): Observable<Booking> {
    const { start, end } = bookingRequest;
    const endDate = end.toISOString();
    console.log({ bookingRequest, end: endDate });

    return this.http.post<Booking>(this.URI, bookingRequest);
  }

  // Método para obtener todas las reservas
  getAllBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(this.URI);
  }

  // Método para obtener una reserva por ID
  getBookingById(id: number): Observable<Booking> {
    return this.http.get<Booking>(`${this.URI}/${id}`);
  }

  getBookingsByUser(userId: number) {
    return this.http.get<Booking[]>(`${this.URI}?userId=${userId}`);
  }

  // Método para eliminar una reserva
  deleteBooking(id: number) {
    return this.http.delete(`${this.URI}/${id}`);
  }
}
