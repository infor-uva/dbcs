import { Injectable } from '@angular/core';
import { LocalStorageService } from './local-storage.service';
import { UserRol } from '../types';

import { jwtDecode } from 'jwt-decode';

export interface AuthInstance {
  id: number;
  name: string;
  email: string;
  rol: UserRol;
}

interface PersistenToken {
  token: string;
}

@Injectable({
  providedIn: 'root',
})
export class SessionService {
  constructor(private storage: LocalStorageService) {}

  private tokenKey = 'token';

  login(session: PersistenToken) {
    this.storage.save(this.tokenKey, session);
  }

  logout() {
    this.storage.remove(this.tokenKey);
  }

  getToken() {
    const savedToken = this.storage.read<PersistenToken>(this.tokenKey);
    if (!savedToken) throw new Error('No session');
    return savedToken.token;
  }

  getSession() {
    const token = this.getToken();
    const r = jwtDecode<{ user: AuthInstance }>(token);
    return r.user;
  }

  isLogged() {
    return !!this.storage.read<PersistenToken>(this.tokenKey);
  }

  isValid() {
    console.warn({ log: this.isLogged() });
    if (!this.isLogged()) return false;
    const token = this.getToken();
    const r = jwtDecode(token);
    // Validate if the token have been expired or not
    return r.exp! > Math.floor(Date.now() / 1000);
  }
}
