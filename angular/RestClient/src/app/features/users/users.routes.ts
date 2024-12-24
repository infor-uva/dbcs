import { AppRoute, UserRolesArray } from '@core/models';
import { UserFormComponent } from './user-form/user-form.component';

export const USERS_ROUTES: AppRoute[] = [
  // Common
  {
    path: '',
    data: { expectedRole: UserRolesArray },
    component: UserFormComponent,
  },
  {
    path: 'edit',
    component: UserFormComponent,
    data: { expectedRole: UserRolesArray },
  },
  {
    path: 'change-passwd',
    component: UserFormComponent,
    data: { expectedRole: UserRolesArray },
  },
  {
    // Usuario administrador de hoteles
    path: 'hotels',
    data: { expectedRole: 'HOTEL_ADMIN' },
    loadChildren: () =>
      import('app/features/hotels').then((m) => m.MANAGERS_ROUTES),
  },
  {
    // Usuario cliente
    path: 'bookings',
    data: { expectedRole: 'CLIENT' },
    loadChildren: () =>
      import('app/features/bookings').then((m) => m.CLIENT_BOOKINGS_ROUTES),
  },
];
