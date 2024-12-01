import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionService } from '../shared/session.service';
import { UserRol, Session } from '../types';
import { map } from 'rxjs';

export const rolGuard: CanActivateFn = (route, state) => {
  const sessionService = inject(SessionService);
  const router = inject(Router);
  // Obtén el rol esperado desde los datos de la ruta
  const expectedRole = route.data?.['expectedRole'];

  // Verifica si el usuario tiene sesión activa
  const session = sessionService.isValid();

  if (!session) {
    console.log('no session');
    router.navigate(['/login']);
    return false;
  }

  return sessionService.getSession().pipe(
    map((session: Session | null) => {
      if (!session) return false;

      if (
        Array.isArray(expectedRole) &&
        (expectedRole as UserRol[]).includes(session.rol)
      ) {
        console.log('Rol in Rol arry');
        return true;
      } else if (session.rol === expectedRole) {
        console.log('Rol valido');
        return true;
      }
      console.log('Unautorizado');

      // Redirige si el usuario no tiene el rol necesario
      router.navigate(['/unauthorized']);
      return false;
    })
  );
};
