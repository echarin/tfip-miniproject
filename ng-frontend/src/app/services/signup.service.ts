// signup.service.ts
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AuthResponse, LoginDTO, SignupDTO } from '../models/dtos';

@Injectable({
  providedIn: 'root'
})
export class SignupService {
  private authUrl: string = environment.sbServerUrl + environment.auth;
  private jsonHeaders = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private httpClient: HttpClient) { }

  signup(signup: SignupDTO): Observable<AuthResponse> {
    const url = `${this.authUrl}/register`;
    return this.httpClient.post<AuthResponse>(url, signup, { headers: this.jsonHeaders });
  }

  login(login: LoginDTO): Observable<AuthResponse> {
    const url = `${this.authUrl}/authenticate`;
    return this.httpClient.post<AuthResponse>(url, login, { headers: this.jsonHeaders });
  }
}
