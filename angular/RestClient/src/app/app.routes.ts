import { HotelListComponent } from './features/hotels/hotel-list/hotel-list.component';
import { BookingComponent } from './features/bookings/booking/booking.component';
import { HotelRegisterComponent } from './features/hotels/hotel-register/hotel-register.component';
import { MainPageComponent } from './features/users/main-page/main-page.component';
import { UserBookingListComponent } from './features/bookings/user-booking-list/user-booking-list.component';
import { UserFormComponent } from './features/users/user-form/user-form.component';
import { UnauthorizedComponent } from './page/unauthorized/unauthorized.component';
import { AppRoute, UserRolesArray } from './core/models';
import { rolGuard, rolGuardChild } from '@core/guards';

export const routes: AppRoute[] = [
  // Auth
  {
    path: '',
    loadChildren: () => import('./features/auth').then((m) => m.AUTH_ROUTES),
  },
  // Hoteles
  {
    path: 'hotels',
    loadChildren: () =>
      import('./features/hotels').then((m) => m.HOTELS_ROUTES),
  },
  // Usuario
  {
    path: 'me',
    canActivate: [rolGuard],
    canActivateChild: [rolGuardChild],
    loadChildren: () => import('./features/users').then((m) => m.USERS_ROUTES),
  },
  // Administrador
  {
    path: 'admin',
    canActivate: [rolGuard],
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
