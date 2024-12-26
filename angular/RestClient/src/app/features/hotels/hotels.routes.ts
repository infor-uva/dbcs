import { AppRoute } from '@core/models';
import { HotelRegisterComponent } from './hotel-register/hotel-register.component';
import { HotelListComponent } from './hotel-list/hotel-list.component';
import { rolGuard } from '@core/guards';

export const HOTELS_ROUTES: AppRoute[] = [
  {
    path: '', // Ruta para la lista de hoteles
    component: HotelListComponent,
  },
  {
    path: 'register', // Registrar nuevo hotel
    component: HotelRegisterComponent,
    canActivate: [rolGuard],
    data: { expectedRole: 'HOTEL_ADMIN' },
  },
  {
    path: ':id', // Hotel concreto
    component: HotelRegisterComponent,
  },
];

export const MANAGERS_ROUTES: AppRoute[] = [
  {
    path: '',
    component: HotelListComponent,
  },
  {
    path: ':id',
    component: HotelRegisterComponent,
  },
  // {
  //   path: ':id/bookings',
  //   component: BookingListComponent,
  // },
  // {
  //   path: ':id/rooms/:id/bookings',
  //   component: BookingListComponent,
  // },
];
