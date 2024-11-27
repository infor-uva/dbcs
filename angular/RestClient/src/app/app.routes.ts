import { Routes } from '@angular/router';
import { HotelListComponent } from './core/features/hotel/hotel-list/hotel-list.component';
import { BookingComponent } from './core/features/bookings/booking/booking.component';
import { HotelRegisterComponent } from './core/features/hotel/hotel-register/hotel-register.component';
import { MainPageComponent } from './core/features/user/main-page/main-page.component';
import { BookingListComponent } from './core/features/bookings/booking-list/booking-list.component';
import { UserBookingListComponent } from './core/features/user/user-booking-list/user-booking-list.component';
import { UserFormComponent} from './core/features/user/user-form/user-form.component';

export const routes: Routes = [
  {
    path: '', // Ruta principal
    component: MainPageComponent, // Componente de la página principal
  },
  {
    path: 'bookings/search',
    component: BookingListComponent,
  },
  {
    path: 'bookings/new',
    component: BookingComponent,
  },
  {
    path: 'users/:id/bookings',
    component: UserBookingListComponent,
  },
  {
    path: 'hotels',
    component: HotelListComponent,
  },
  {
    path: 'hotels/new',
    component: HotelRegisterComponent,
  },
  {
    path: 'hotels/:id',
    component: HotelRegisterComponent,
  },
  {
    path: 'users/:id',
    component: UserFormComponent,
  },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full',
  },
];
