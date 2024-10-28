import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import hotels from '../../mocks/hotels.json';
import { Hotel } from '../../types';
import { Observable } from 'rxjs';
import { User } from '../../types';

@Injectable({
  providedIn: 'root',
})
export class ClienteApiRestService {
  private static readonly BASE_URI = 'http://localhost:8080';
  private static readonly HOTEL_URI = `${ClienteApiRestService.BASE_URI}/hotels`;
  private static readonly USER_URI = `${ClienteApiRestService.BASE_URI}/users`;
  constructor(private http: HttpClient) {}

  getAllHotels() {
    const url = `${ClienteApiRestService.HOTEL_URI}`;
    return this.http.get<Hotel[]>(url, { observe: 'response' });
  }

  deleteHotel(id: number) {
    const url = `${ClienteApiRestService.HOTEL_URI}/${id}`;
    return this.http.delete(url, { observe: 'response', responseType: 'text' });
  }

  addHotel(hotel: Hotel) {
    const url = `${ClienteApiRestService.HOTEL_URI}`;
    return this.http.post(url, hotel, {
      observe: 'response',
      responseType: 'text',
    });
  }

  alterRoomAvailability(
    hotelId: number,
    roomId: number,
    availability: boolean
  ) {
    const url = `${ClienteApiRestService.HOTEL_URI}/${hotelId}/rooms/${roomId}`;
    return this.http.patch(
      url,
      { available: availability },
      {
        observe: 'response',
        responseType: 'text',
      }
    );
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>('http://localhost:8080/users', { observe: 'body' });
  }
  
}
