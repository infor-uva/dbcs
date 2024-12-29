import { UnauthorizedComponent } from './page/unauthorized/unauthorized.component';
import { AppRoute } from './core/models/Route.interface';
import { rolGuardChild } from '@core/guards';
import { AUTH_ROUTES } from '@features/auth';
export const routes: AppRoute[] = [
  // Auth
  ...AUTH_ROUTES,
  // Hoteles
  {
    path: 'hotels',
    loadChildren: () =>
      import('./features/hotels').then((m) => m.HOTELS_ROUTES),
  },
  // Usuario
  {
    path: 'me',
    canActivateChild: [rolGuardChild],
    loadChildren: () => import('./features/users').then((m) => m.USERS_ROUTES),
  },
  // Administrador
  {
    path: 'admin',
    canActivateChild: [rolGuardChild],
    data: { expectedRole: 'ADMIN' },
    loadChildren: () => import('./features/admin').then((m) => m.ADMIN_ROUTES),
  },
  // Página no autorizada
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
