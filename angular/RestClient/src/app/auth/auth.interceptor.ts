import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { LocalStorageService } from '../shared/local-storage.service';

const excluded = ['/login', '/register'];

function isExcludedUrl(url: string) {
  return excluded.some((excluded) => url.includes(excluded));
}

export const authRequest: HttpInterceptorFn = (req, next) => {
  // Obtener el token desde localStorage (o cualquier otro mecanismo)
  const storage = inject(LocalStorageService); // Obtener instancia del servicio
  const token = storage.read<{ token: string }>('token');

  if (isExcludedUrl(req.url) || !token) {
    return next(req); // No modificar la solicitud
  }

  // Clonar la solicitud y agregar el token al encabezado si existe
  const authReq = token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token.token}`,
          'Content-Type': 'application/json',
        },
      })
    : req;

  // Pasar la solicitud modificada al siguiente manejador
  return next(authReq);
};
