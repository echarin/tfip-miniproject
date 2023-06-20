// auth.service.ts
import { Injectable } from '@angular/core';
import { AuthResponse } from '../models/dtos';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor() { }

  storeToken(authResponse: AuthResponse) {
    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('expiresAt', String(authResponse.expiresAt));
  }
}
