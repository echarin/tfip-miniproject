// auth.interceptor.ts
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, throwError } from 'rxjs';
import { TokenService } from '../services/token.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private tokenService: TokenService, private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const auth = this.tokenService.getAuth();
    if (auth) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${auth.token}`
        }
      });
    }
    return next.handle(request)
      .pipe(catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.tokenService.removeAuth();
          this.router.navigate([''], { queryParams: { message: 'please log in again. (AuthInterceptor)'}});
        }

        return throwError(() => error);
      }));
  }
}
