import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenStorageService } from '../services/token-storage.service';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokens = inject(TokenStorageService);
  const auth = inject(AuthService);

  if (!tokens.token || tokens.isExpired()) {
    auth.logout();
    return next(req);
  }

  const authReq = req.clone({ setHeaders: { Authorization: `Bearer ${tokens.token}` } });
  return next(authReq);
};
