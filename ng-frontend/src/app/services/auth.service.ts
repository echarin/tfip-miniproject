// auth.service.ts
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AuthResponse, AuthRequest } from '../models/dtos';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authUrl: string = environment.sbServerUrl + environment.auth;
  private jsonHeaders = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private httpClient: HttpClient) { }

  signup(signup: AuthRequest): Observable<AuthResponse> {
    const url = `${this.authUrl}/register`;
    return this.httpClient.post<AuthResponse>(url, signup, { headers: this.jsonHeaders });
  }

  login(login: AuthRequest): Observable<AuthResponse> {
    const url = `${this.authUrl}/authenticate`;
    return this.httpClient.post<AuthResponse>(url, login, { headers: this.jsonHeaders });
  }
}