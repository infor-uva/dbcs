import { ActivatedRoute } from '@angular/router';

export function getBasePath(route: ActivatedRoute): string {
  const urlSegments = route.snapshot.url;
  if (urlSegments[0].path === 'me') {
    return '/me';
  } else if (
    urlSegments.length >= 3 &&
    urlSegments[0].path === 'admin' &&
    urlSegments[1].path === 'users' &&
    urlSegments[2]
  ) {
    return `/admin/users/${urlSegments[2]}`; // Devuelve la ruta con el ID del usuario
  } else if (urlSegments[0].path === 'admin') {
    return '/me';
  }
  throw new Error('Invalid route structure'); // Manejo de errores si la URL no es válida
}
