import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { LoginDTO, LoginResponse, SignupDTO, SignupResponse } from '../models/dtos';

@Injectable({
  providedIn: 'root'
})
export class SignupService {
  private authUrl: string = environment.sbServerUrl + environment.auth;
  private jsonHeaders = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private httpClient: HttpClient) { }

  signup(signup: SignupDTO): Observable<SignupResponse> {
    const url = `${this.authUrl}/register`;
    return this.httpClient.post<SignupResponse>(url, signup, { headers: this.jsonHeaders });
  }

  login(login: LoginDTO): Observable<LoginResponse> {
    const url = `${this.authUrl}/authenticate`;
    return this.httpClient.post<LoginResponse>(url, login, { headers: this.jsonHeaders });
  }
}
