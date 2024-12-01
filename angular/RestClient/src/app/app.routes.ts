import { Route, Routes } from '@angular/router';
import { HotelListComponent } from './core/features/hotel/hotel-list/hotel-list.component';
import { BookingComponent } from './core/features/bookings/booking/booking.component';
import { HotelRegisterComponent } from './core/features/hotel/hotel-register/hotel-register.component';
import { MainPageComponent } from './core/features/user/main-page/main-page.component';
import { BookingListComponent } from './core/features/bookings/booking-list/booking-list.component';
import { UserBookingListComponent } from './core/features/user/user-booking-list/user-booking-list.component';
import { UserFormComponent } from './core/features/user/user-form/user-form.component';
import { UnauthorizedComponent } from './page/unauthorized/unauthorized.component';
import { rolGuard } from './security/rol.guard';
import { UserRol, UserRolesArray } from './types';

interface RouteData {
  expectedRole: UserRol | UserRol[];
}

type AppRoute = Omit<Route, 'data'> & {
  data?: RouteData;
};

export const routes: AppRoute[] = [
  // Auth
  {
    path: 'login',
    component: UserFormComponent,
  },
  {
    path: 'register',
    component: UserFormComponent,
  },

  // Hoteles
  {
    path: 'hotels', // Ruta para la lista de hoteles
    component: HotelListComponent,
  },
  {
    path: 'hotels/register', // Registrar nuevo hotel
    component: HotelRegisterComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'HOTEL_ADMIN' },
  },
  {
    path: 'hotels/:id', // Hotel concreto
    component: HotelRegisterComponent,
  },

  // Usuario
  {
    path: 'me', // Main
    canActivate: [rolGuard],
    data: { expectedRole: UserRolesArray },
    component: UserFormComponent,
  },
  {
    path: 'me/edit', // Main
    component: UserFormComponent,
    canActivate: [rolGuard],
    data: { expectedRole: UserRolesArray },
  },
  {
    path: 'me/change-passwd', // Main
    component: UserFormComponent,
    canActivate: [rolGuard],
    data: { expectedRole: UserRolesArray },
  },
  // Usuario HOTEL admin
  {
    path: 'me/hotels',
    component: HotelListComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'HOTEL_ADMIN' },
  },
  {
    path: 'me/hotels/:id',
    component: HotelRegisterComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'HOTEL_ADMIN' },
  },
  // {
  //   path: 'me/hotels/:id/bookings',
  //   component: BookingListComponent,
  // },
  // {
  //   path: 'me/hotels/:id/rooms/:id/bookings',
  //   component: BookingListComponent,
  // },

  // Usuario Cliente
  {
    path: 'me/bookings',
    component: UserBookingListComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'CLIENT' },
  },
  {
    path: 'me/bookings/:id',
    component: BookingComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'CLIENT' },
  },
  {
    path: 'me/bookings/new',
    component: BookingComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'CLIENT' },
  },

  // Administrador
  {
    path: 'admin', // Main
    component: UserFormComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'ADMIN' },
  },
  {
    path: 'admin/users', // Main
    component: MainPageComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'ADMIN' },
  },
  {
    path: 'admin/users/:id', // Main
    component: UserFormComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'ADMIN' },
  },
  {
    path: 'admin/users/:id/edit', // Main
    component: UserFormComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'ADMIN' },
  },
  {
    path: 'admin/users/:id/change-passwd', // Main
    component: UserFormComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'ADMIN' },
  },
  {
    path: 'admin/users/:id/bookings',
    component: UserBookingListComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'ADMIN' },
  },
  // {
  //   path: 'admin/users/:id/bookings/:bookingId',
  //   component: BookingComponent,
  // },
  {
    path: 'admin/users/:id/hotels',
    component: HotelListComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'ADMIN' },
  },
  {
    path: 'admin/users/:userId/hotels/:id',
    component: HotelRegisterComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'ADMIN' },
  },
  // {
  //   path: 'admin/users/:userId/hotels/:id/bookings',
  //   component: BookingListComponent,
  //   canActivate: [rolGuard],
  //   data: { expectedRole: 'ADMIN' },
  // },
  // {
  //   path: 'admin/users/:userId/hotels/:hotelId/rooms/:id/bookings',
  //   component: BookingListComponent,
  //   canActivate: [rolGuard],
  //   data: { expectedRole: 'ADMIN' },
  // },

  {
    path: 'unauthorized',
    component: UnauthorizedComponent,
  },
  {
    path: '**',
    redirectTo: '/login',
    pathMatch: 'full',
  },
];
