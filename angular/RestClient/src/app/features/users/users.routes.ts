import { UserFormComponent } from './user-form/user-form.component';
import { UserFormRoute } from './types/UserFormData';

export const USERS_ROUTES: UserFormRoute[] = [
  // Common
  {
    path: '',
    data: {
      mode: {
        formMode: 'VIEW',
      },
    },
    component: UserFormComponent,
  },
  {
    path: 'edit',
    data: {
      mode: {
        formMode: 'EDIT',
      },
    },
    component: UserFormComponent,
  },
  {
    path: 'change-passwd',
    data: {
      mode: {
        formMode: 'PASSWORD',
      },
    },
    component: UserFormComponent,
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
