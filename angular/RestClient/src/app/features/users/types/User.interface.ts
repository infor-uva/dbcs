import { Booking } from '@features/bookings';
import { Hotel } from '@features/hotels';

export interface User {
  id: number;
  name: string;
  email: String;
  rol: UserRol;
}

export type UserRol = 'CLIENT' | 'HOTEL_ADMIN' | 'ADMIN';
export const UserRolesArray: UserRol[] = ['CLIENT', 'HOTEL_ADMIN', 'ADMIN'];


export interface Client extends User {
  status: ClientState;
  bookings: Booking[];
}

export interface HotelAdmin extends User {
  hotels: Hotel[];
}

export type UserStateFilter = 'All' | ClientState;

export const ClientStateArray = [
  'NO_BOOKINGS',
  'WITH_ACTIVE_BOOKINGS',
  'WITH_INACTIVE_BOOKINGS',
] as const;
export type ClientState = 'NO_BOOKINGS' | 'WITH_ACTIVE_BOOKINGS' |  'WITH_INACTIVE_BOOKINGS';
