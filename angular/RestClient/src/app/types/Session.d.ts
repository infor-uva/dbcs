export interface Session {
  id: number;
  name: string;
  email: string;
  rol: UserRol;
}

interface PersistenToken {
  token: string;
  session?: Session;
}
