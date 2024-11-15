import { isPlatformBrowser } from '@angular/common';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LocalStorageService {
  // isBrowser: boolean;

  // constructor(@Inject(PLATFORM_ID) private platformId: Object) {
  //   this.isBrowser = isPlatformBrowser(this.platformId);
  // }

  save(key: string, value: any) {
    // if (!this.isBrowser) return;
    localStorage.setItem(key, JSON.stringify(value));
  }

  read<T>(key: string) {
    // if (!this.isBrowser) return null;

    const json = localStorage.getItem(key);
    const ret = json ? (JSON.parse(json) as T) : null;
    return ret;
  }

  consume<T>(key: string) {
    // if (!this.isBrowser) return null;

    const value = this.read<T>(key);
    if (value !== null) {
      this.remove(key);
    }
    return value;
  }

  remove(key: string) {
    // if (!this.isBrowser) return;

    localStorage.removeItem(key);
  }
}
