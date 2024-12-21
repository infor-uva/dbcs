import { Route } from '@angular/router';
import { UserRol } from './User.interface';

interface RouteData {
  expectedRole: UserRol | UserRol[];
}

export type AppRoute = Omit<Route, 'data'> & {
  data?: RouteData;
};
