import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { Booking } from '@features/bookings';
import { HotelClientService } from '../hotels/hotel-client.service';
import { switchMap, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class BookingClientService {
  private URI = environment.bookingAPI;

  constructor(
    private http: HttpClient,
    private hotelClientService: HotelClientService // Inyectamos el servicio HotelClientService
  ) {}

  // Método para crear una nueva reserva
  createBooking(bookingRequest: Booking): Observable<Booking> {
    const { hotelId, start, end, userId, roomId } = bookingRequest;
    
    if (!hotelId) {
      console.error("hotelId is undefined");
      return throwError(() => new Error("hotelId is undefined"));
    }
  
    // Llamamos al servicio HotelClientService para obtener el hotel y su managerId
    return this.hotelClientService.getHotel(hotelId).pipe(
      map((hotel) => {
        const managerId = hotel.managerId;
        console.log("Manager ID:", managerId);  // Verifica que el managerId es correcto
        
        // Retornamos el objeto bookingRequest con el managerId actualizado
        return { ...bookingRequest, managerId };
      }),
      switchMap((updatedBookingRequest) => {
        console.log("Final bookingRequest with managerId:", updatedBookingRequest);  // Verifica el objeto final
        return this.http.post<Booking>(this.URI, updatedBookingRequest);
      })
    );
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
