import { AppRoute } from '@core/models';

export type UserFormMode =
  | 'REGISTER'
  | 'LOGIN'
  | 'PASSWORD'
  | 'VIEW'
  | 'EDIT'
  | 'OTHER';

export type ModeType = {
  formMode: UserFormMode;
  admin?: boolean;
};

export type UserFormRoute = AppRoute<{ mode?: ModeType }>;
