import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LocalStorageService {
  save(key: string, value: object) {
    const content = JSON.stringify(value);
    localStorage.setItem(key, content);
  }

  read<T>(key: string) {
    const json = localStorage.getItem(key);
    const ret = json ? (JSON.parse(json) as T) : null;
    return ret;
  }

  consume<T>(key: string) {
    const value = this.read<T>(key);
    if (value !== null) {
      this.remove(key);
    }
    return value;
  }

  remove(key: string) {
    localStorage.removeItem(key);
  }
}
