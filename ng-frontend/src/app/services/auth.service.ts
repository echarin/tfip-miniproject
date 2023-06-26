// auth.service.ts
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject, tap } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AuthResponse, AuthRequest } from '../models/auth-dtos';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authUrl: string = environment.sbServerUrl + environment.auth;
  private jsonHeaders = new HttpHeaders().set('Content-Type', 'application/json');
  userLoggedIn: Subject<string> = new Subject<string>();

  constructor(
    private httpClient: HttpClient,
    private tokenSvc: TokenService
  ) { }

  signup(signup: AuthRequest): Observable<AuthResponse> {
    const url = `${this.authUrl}/register`;
    return this.httpClient.post<AuthResponse>(url, signup, { headers: this.jsonHeaders })
      .pipe(tap((authResponse: AuthResponse) => {
        this.tokenSvc.storeAuth(authResponse);
        this.userLoggedIn.next(authResponse.userId);
      }));
  }

  login(login: AuthRequest): Observable<AuthResponse> {
    const url = `${this.authUrl}/authenticate`;
    return this.httpClient.post<AuthResponse>(url, login, { headers: this.jsonHeaders })
      .pipe(tap((authResponse: AuthResponse) => {
        this.tokenSvc.storeAuth(authResponse);
        this.userLoggedIn.next(authResponse.userId);
      }));
  }  
}