import { Injectable } from '@angular/core';
import { LocalStorageService } from '../storage/local-storage.service';
import { PersistenToken, Session, UserRol } from '../../models';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { jwtDecode } from 'jwt-decode';
import { AuthClientService } from '../api/auth/auth-client.service';
import { Router } from '@angular/router';

interface JWTDecoded {
  userId: number;
  rol: UserRol;
  name: string;
  email: string;
  iat: number;
  exp: number;
}

@Injectable({
  providedIn: 'root',
})
export class SessionService {
  private tokenKey = 'token';
  private session$: BehaviorSubject<Session | null>;
  mainPage = '/me';

  constructor(
    private router: Router,
    private storage: LocalStorageService,
    private authService: AuthClientService
  ) {
    // Inicializar el estado de sesión desde el token almacenado
    const initialSession = this.loadSessionFromToken();

    this.session$ = new BehaviorSubject<Session | null>(initialSession);
  }

  getMainPage(rol: UserRol) {
    return rol === 'ADMIN' ? '/admin' : '/me';
  }

  private setSession(resp: any) {
    const decoded = jwtDecode<JWTDecoded>(resp.token);
    const user: Session = { ...decoded, id: decoded.userId };
    this.session$.next(user);
    this.storage.save(this.tokenKey, { ...resp, session: user });
    const mainPage = this.getMainPage(user.rol as UserRol);
    return { ...resp, mainPage };
  }

  /**
   * Realiza el login y actualiza el estado de la sesión.
   */
  login(email: string, password: string): Observable<any> {
    return this.authService.login(email, password).pipe(
      map((response) => this.setSession(response)),
      catchError((error) => {
        console.error('Login failed', error);
        return throwError(() => new Error('Login failed'));
      })
    );
  }

  /**
   * Realiza el registro, guarda el token y actualiza el estado de la sesión.
   */
  register(
    name: string,
    email: string,
    password: string,
    rol: UserRol
  ): Observable<any> {
    return this.authService.register(name, email, password, rol).pipe(
      map((r) => this.setSession(r)),
      catchError((error) => {
        console.error('Registration failed', error);
        return throwError(() => new Error('Registration failed'));
      })
    );
  }

  /**
   * Realiza el logout, elimina el token y limpia el estado de sesión.
   */
  logout(): void {
    this.storage.remove(this.tokenKey);
    this.session$.next(null);
    this.router.navigate(['/login']);
  }

  getSaved() {
    return this.storage.read<PersistenToken>(this.tokenKey);
  }

  /**
   * Obtiene el token almacenado. Lanza un error si no hay sesión activa.
   */
  getToken(): string {
    const saved = this.getSaved();
    if (!saved) {
      throw new Error('No session');
    }
    return saved.token;
  }

  /**
   * Proporciona un Observable del estado de la sesión.
   */
  getSession(): Observable<Session | null> {
    return this.session$.asObservable();
  }

  updateData(data: Partial<Session>) {
    // const session: Session = { ...this.session$.getValue() } as Session;
    const saved = this.getSaved();
    console.log({ saved, data });

    if (!saved || data.id !== saved.session?.id) return;
    const session = { ...saved.session, ...data } as Session;
    this.storage.save(this.tokenKey, {
      ...saved,
      session,
    });
    this.session$.next(session);
  }

  /**
   * Verifica si el usuario está logueado.
   */
  isLogged(): boolean {
    return !!this.session$.getValue();
  }

  /**
   * Valida si el token almacenado es válido (no expirado).
   */
  isValid(): boolean {
    if (!this.isLogged()) return false;

    try {
      const token = this.getToken();
      const decoded = jwtDecode<{ exp: number }>(token);
      const valid = decoded.exp > Math.floor(Date.now() / 1000);
      console.log({ valid, rem: decoded.exp - Math.floor(Date.now() / 1000) });
      if (!valid) {
        this.logout();
      }
      return valid;
    } catch (error) {
      console.error('Token validation failed', error);
      return false;
    }
  }

  /**
   * Carga la sesión desde el token almacenado.
   */
  private loadSessionFromToken(): Session | null {
    try {
      return this.getSaved()!.session!;
    } catch {
      return null; // Retornar null si no hay token válido.
    }
  }
}
