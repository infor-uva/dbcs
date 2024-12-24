import { AppRoute } from '@core/models';
import { MainPageComponent } from 'app/features/users/main-page/main-page.component';
import { UserFormComponent } from 'app/features/users/user-form/user-form.component';
import { USERS_ROUTES } from 'app/features/users/users.routes';

function getRoutesWithoutRol(routes: AppRoute[]) {
  return routes.map((r) => {
    if (r.data?.expectedRole) {
      const { data, ...rest } = r;
      return { ...rest };
    }
    return r;
  });
}

export const ADMIN_ROUTES: AppRoute[] = [
  {
    path: '', // Main
    component: UserFormComponent,
  },
  {
    path: 'users',
    children: [
      {
        path: '',
        component: MainPageComponent,
      },
      {
        path: ':id',
        children: getRoutesWithoutRol(USERS_ROUTES),
      },
    ],
  },
  // {
  //   path: 'users/:id/bookings/:bookingId',
  //   component: BookingComponent,
  // },
  // {
  //   path: 'users/:userId/hotels/:id/bookings',
  //   component: BookingListComponent,
  // },
  // {
  //   path: 'users/:userId/hotels/:hotelId/rooms/:id/bookings',
  //   component: BookingListComponent,
  // },
];
