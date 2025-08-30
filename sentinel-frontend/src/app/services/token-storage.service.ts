import { Injectable } from '@angular/core';

const ACCESS = 'sentinel_access';

@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  get token() { return localStorage.getItem(ACCESS); }
  set token(v: string | null) { v ? localStorage.setItem(ACCESS, v) : localStorage.removeItem(ACCESS); }
  clear() { localStorage.removeItem(ACCESS); }

  isExpired(token = this.token): boolean {
    try {
      if (!token) return true;
      const { exp } = JSON.parse(atob(token.split('.')[1] || ''));
      return !exp || Date.now() >= exp * 1000;
    } catch { return true; }
  }
  isAuthenticated(): boolean { return !!this.token && !this.isExpired(); }
}
