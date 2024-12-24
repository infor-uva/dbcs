import { AppRoute } from '@core/models';
import { BookingComponent } from './booking/booking.component';
import { UserBookingListComponent } from './user-booking-list/user-booking-list.component';

export const BOOKINGS_ROUTES: AppRoute[] = [];
export const CLIENT_BOOKINGS_ROUTES: AppRoute[] = [
  {
    path: '',
    component: UserBookingListComponent,
    data: { expectedRole: 'CLIENT' },
  },
  {
    path: ':id',
    component: BookingComponent,
    data: { expectedRole: 'CLIENT' },
  },
  {
    path: 'new',
    component: BookingComponent,
    data: { expectedRole: 'CLIENT' },
  },
];
