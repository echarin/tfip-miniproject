// auth.service.ts
import { Injectable } from '@angular/core';
import { AuthResponse } from '../models/dtos';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  constructor() { }

  storeToken(authResponse: AuthResponse) {
    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('expiresAt', String(authResponse.expiresAt));
  }

  getToken(): string | null {
    const token = localStorage.getItem('token');
    const expiresAt = localStorage.getItem('expiresAt');

    if (token && expiresAt) {
      if (!this.isTokenExpired(Number(expiresAt))) {
        return token;
      }
    }

    // Otherwise, token is expired or not available, so clear local storage
    this.removeToken();
    return null;
  }

  removeToken() {
    localStorage.removeItem('token');
    localStorage.removeItem('expiresAt');
  }

  isTokenExpired(expiresAt: number): boolean {
    return new Date().getTime() > expiresAt;
  }
}