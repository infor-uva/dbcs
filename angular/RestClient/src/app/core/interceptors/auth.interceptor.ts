import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { SessionService } from '@core/services/session/session.service';

const excluded = ['/login', '/register'];

function isExcludedUrl(url: string) {
  return excluded.some((excluded) => url.includes(excluded));
}

export const authRequest: HttpInterceptorFn = (req, next) => {
  // Obtener el token desde localStorage (o cualquier otro mecanismo)
  const session = inject(SessionService); // Obtener instancia del servicio
  const isLogged = session.isLogged();

  if (isExcludedUrl(req.url) || !isLogged) {
    return next(req); // No modificar la solicitud
  }

  const token = session.getToken();

  console.log('TOKEN:', { token });

  // Clonar la solicitud y agregar el token al encabezado si existe
  const authReq = token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      })
    : req;

  // Pasar la solicitud modificada al siguiente manejador
  return next(authReq);
};
