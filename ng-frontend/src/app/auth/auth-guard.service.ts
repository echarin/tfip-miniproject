// auth.guard.ts
import { inject } from '@angular/core';
import { Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { TokenService } from '../services/token.service';

export function canActivate(state: RouterStateSnapshot): Observable<boolean | UrlTree> {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  if (tokenService.isAuthenticated()) {
    return of(true);
  } else {
    router.navigate([''], { queryParams: { 
      message: 'please log in again. (canActivate)',
      returnUrl: state.url,
    }});
    return of(false);
  }
}
