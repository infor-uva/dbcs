import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Hotel, Room } from '../types';

@Injectable({
  providedIn: 'root',
})
export class HotelClientService {
  private readonly URI = environment.hotelAPI;
  constructor(private http: HttpClient) {}

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
    return this.http.delete(url, { observe: 'response', responseType: 'text' });
  }

  addHotel(hotel: Hotel) {
    const url = `${this.URI}`;
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
    const url = `${this.URI}/${hotelId}/rooms/${roomId}`;
    return this.http.patch(
      url,
      { available: availability },
      {
        observe: 'response',
        responseType: 'text',
      }
    );
  }

  getRoomsAvailableInDateRange(hotelId: number, start: Date, end: Date) {
    const startStr = start.toISOString().split('T')[0];
    const endStr = end.toISOString().split('T')[0];
    const url = `${this.URI}/${hotelId}/rooms?start=${startStr}&end=${endStr}`;
    return this.http.get<Room[]>(url);
  }
}
