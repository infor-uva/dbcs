import { UserFormComponent } from 'app/features/users';
import { UserFormRoute } from 'app/features/users/types/UserFormData';

export const AUTH_ROUTES: UserFormRoute[] = [
  {
    path: 'login',
    data: {
      mode: {
        formMode: 'LOGIN',
      },
    },
    component: UserFormComponent,
  },
  {
    path: 'register',
    data: {
      mode: {
        formMode: 'REGISTER',
      },
    },
    component: UserFormComponent,
  },
];
