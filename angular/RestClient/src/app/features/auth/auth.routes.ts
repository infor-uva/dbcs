import { AppRoute } from '@core/models';
import { UserFormComponent } from 'app/features/users';

export const AUTH_ROUTES: AppRoute[] = [
  {
    path: 'login',
    component: UserFormComponent,
  },
  {
    path: 'register',
    component: UserFormComponent,
  },
];
