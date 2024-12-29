import { AppRoute } from '@core/models/Route.interface';
import { MainPageComponent } from 'app/features/users/main-page/main-page.component';
import { UserFormRoute } from 'app/features/users/types/UserFormData';
import { UserFormComponent } from 'app/features/users/user-form/user-form.component';
import { COMMON_USERS_ROUTES } from 'app/features/users/users.routes';

function changeToAdminScope(routes: UserFormRoute[]) {
  return routes.map((r) => {
    if (r.data) {
      const { data, ...rest } = r;
      data.expectedRole = undefined;
      if (data.mode) {
        data.mode.admin = true;
      }
      return { data, ...rest };
    }
    return r;
  });
}

const mainRoute: UserFormRoute = {
  path: '', // Main
  data: {
    mode: {
      formMode: 'VIEW',
      admin: true,
    },
  },
  component: UserFormComponent,
};

export const ADMIN_ROUTES: AppRoute[] = [
  mainRoute,
  {
    path: 'users',
    children: [
      {
        path: '',
        component: MainPageComponent,
      },
      {
        path: ':id',
        children: changeToAdminScope(COMMON_USERS_ROUTES),
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
