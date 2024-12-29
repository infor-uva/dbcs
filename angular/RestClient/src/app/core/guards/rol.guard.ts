import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateChildFn,
  CanActivateFn,
  Router,
} from '@angular/router';
import { Session } from '@core/models/Session.interface';
import { UserRol } from '../../features/users/types/User.interface';
import { SessionService } from '@core/services';

import { map } from 'rxjs';

/**
 * Obtiene el rol de la ruta padre si el hijo no define uno.
 * Navega hacia los ancestros buscando un rol especificado en `data`.
 */
function getInheritedRole(route: ActivatedRouteSnapshot): string | undefined {
  let parent = route.parent; // Comenzar desde el padre inmediato
  while (parent) {
    if (parent.data && parent.data['rol']) {
      return parent.data['rol']; // Retorna el primer rol encontrado
    }
    parent = parent.parent; // Continuar hacia arriba en el árbol
  }
  return undefined; // No se encontró un rol definido en los ancestros
}

function verifyRol(expectedRole: UserRol) {
  const sessionService = inject(SessionService);
  const router = inject(Router);
  // Verifica si el usuario tiene sesión activa
  const session = sessionService.isValid();

  if (!session) {
    console.log('no session');
    router.navigateByUrl(`/login?redirect=${router.url}`);
    return false;
  }

  if (!expectedRole) {
    console.log('any rol required');
    return true;
  }

  return sessionService.getSession().pipe(
    map((session: Session | null) => {
      if (!session) return false;

      if (
        Array.isArray(expectedRole) &&
        !(expectedRole as UserRol[]).includes(session.rol)
      ) {
        console.log('Rol in Rol array');
        return true;
      } else if (session.rol === expectedRole) {
        console.log('Rol valido');
        return true;
      }
      console.log('Unauthorized');

      // Redirige si el usuario no tiene el rol necesario
      router.navigate(['/unauthorized']);
      return false;
    })
  );
}

export const rolGuard: CanActivateFn = (route, state) => {
  // Obtén el rol esperado desde los datos de la ruta
  const expectedRole = route.data?.['expectedRole'];
  return verifyRol(expectedRole);
};

export const rolGuardChild: CanActivateChildFn = (childRoute, state) => {
  // Obtener el rol de la ruta hija si está especificado y en caso
  // de no especificarse se busca en el/los padres
  let requiredRol =
    childRoute.data['expectedRole'] ?? getInheritedRole(childRoute);

  // Si no hay rol especificado se supone libre de verificación
  if (!requiredRol) return true;

  // Verificar si el usuario tiene el rol requerido
  return verifyRol(requiredRol);
};
