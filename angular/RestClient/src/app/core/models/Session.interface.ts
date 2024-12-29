import { UserRol } from '../../features/users/types/User.interface';

export interface Session {
  id: number;
  name: string;
  email: string;
  rol: UserRol;
}

export interface PersistenToken {
  token: string;
  session?: Session;
}
