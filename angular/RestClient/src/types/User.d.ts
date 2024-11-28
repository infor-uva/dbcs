export interface User {
  id: number;
  name: string;
  email: String;
  rol: UserRol;
}

export interface Client extends User {
  status: UserState;
  // bookings: number[] // Booking[]
}

export interface HotelAdmin extends User {
  // hotels: number[] // Hotel[]
}

export type UserRol = 'ADMIN' | 'CLIENT' | 'HOTEL_ADMIN';

export type UserStateFilter = 'All' | UserState;

export type UserState =
  | 'NO_BOOKINGS'
  | 'WITH_ACTIVE_BOOKINGS'
  | 'WITH_INACTIVE_BOOKINGS';
