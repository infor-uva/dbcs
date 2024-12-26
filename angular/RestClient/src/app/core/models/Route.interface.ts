import { Route } from '@angular/router';
import { UserRol } from '../../features/users/types/User.interface';

type RolledRoute = {
  expectedRole?: UserRol | UserRol[];
};

export type AppRoute<T = {}> = Omit<Route, 'data'> & {
  data?: RolledRoute & T;
};
