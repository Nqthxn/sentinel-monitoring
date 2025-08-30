import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest, LoginResponse } from '../models/auth.models';
import { TokenStorageService } from './token-storage.service';

export interface AuthState { isAuthenticated: boolean; username: string | null; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private tokens = inject(TokenStorageService);

  private _state = new BehaviorSubject<AuthState>({ isAuthenticated: this.tokens.isAuthenticated(), username: null });
  readonly authState$ = this._state.asObservable();

  private setState(p: Partial<AuthState>) { this._state.next({ ...this._state.value, ...p }); }

  login(body: LoginRequest) {
    return this.http.post<LoginResponse>(`${environment.apiBaseUrl}/api/v1/auth/authenticate`, body)
      .pipe(tap(res => {
        this.tokens.token = res.token;
        this.setState({ isAuthenticated: true, username: this.decodeSub(res.token) });
      }));
  }

  logout() { this.tokens.clear(); this.setState({ isAuthenticated: false, username: null }); }

  initFromStorage() {
    if (this.tokens.isAuthenticated()) this.setState({ isAuthenticated: true, username: this.decodeSub(this.tokens.token!) });
    else this.logout();
  }

  private decodeSub(token: string | null): string | null {
    try { return token ? JSON.parse(atob(token.split('.')[1] || '')).sub ?? null : null; } catch { return null; }
  }
}
