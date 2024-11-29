import { Routes } from '@angular/router';
import { HotelListComponent } from './core/features/hotel/hotel-list/hotel-list.component';
import { BookingComponent } from './core/features/bookings/booking/booking.component';
import { HotelRegisterComponent } from './core/features/hotel/hotel-register/hotel-register.component';
import { MainPageComponent } from './core/features/user/main-page/main-page.component';
import { BookingListComponent } from './core/features/bookings/booking-list/booking-list.component';
import { UserBookingListComponent } from './core/features/user/user-booking-list/user-booking-list.component';
import { UserFormComponent } from './core/features/user/user-form/user-form.component';

import { LoginComponent } from './core/features/auth/login/login.component';
import { UserFormComponent } from './core/features/user/user-form/user-form.component';

export const routes: Routes = [
  {
    path: '', // Ruta principal
    component: MainPageComponent,
  },
  // auth
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
  },
  // Hoteles
  {
    path: 'hotels', // Ruta para la lista de hoteles
  },
  {
    path: 'hotels/register', // Registrar nuevo hotel
  },
  {
    path: 'hotels/:id', // Hotel concreto
  },
  {
    path: 'hotels/:id/edit', // Modificar hotel
  },
  // Usuario
  {
    path: 'me', // Main
  },
  {
    path: 'me/edit', // Main
  },
  // Usuario HOTEL admin
  {
    path: 'me/hotels',
  },
  {
    path: 'me/hotels/:id',
  },
  {
    path: 'me/hotels/:id/bookings',
  },
  {
    path: 'me/hotels/:id/rooms',
  },
  {
    path: 'me/hotels/:id/rooms/:id',
  },
  {
    path: 'me/hotels/:id/rooms/:id/bookings',
  },
  // Usuario Cliente
  {
    path: 'me/bookings',
  },
  {
    path: 'me/bookings/:id',
  },
  {
    path: 'me/bookings/new',
  },
  // Administrador
  {
    path: 'admin', // Main
  },
  {
    path: 'admin/users', // Main
  },
  {
    path: 'admin/users/:id', // Main
  },

  // ! OTRO // NO MIRAR

  // {
  //   path: 'bookings/search',
  //   component: BookingListComponent,
  // },
  // {
  //   path: 'bookings/new',
  //   component: BookingComponent,
  // },
  // {
  //   path: 'users/:id/bookings',
  //   component: UserBookingListComponent,
  // },
  // {
  //   path: 'hotels',
  //   component: HotelListComponent,
  // },
  // {
  //   path: 'hotels/new',
  //   component: HotelRegisterComponent,
  // },
  // {
  //   path: 'hotels/:id',
  //   component: HotelRegisterComponent,
  // },
  // {
  //   path: 'users/:id',
  //   component: UserFormComponent,
  // },
  // {
  //   path: 'register',
  //   component: UserFormComponent,
  // },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full',
  },
];
