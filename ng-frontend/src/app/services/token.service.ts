// token.service.ts
import { Injectable } from '@angular/core';
import { AuthResponse } from '../models/dtos';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  constructor() { }

  storeAuth(authResponse: AuthResponse) {
    localStorage.setItem('userId', authResponse.userId);
    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('expiresAt', String(authResponse.expiresAt));
  }

  getAuth(): AuthResponse | null {
    const userId = localStorage.getItem('userId');
    const token = localStorage.getItem('token');
    const expiresAt = localStorage.getItem('expiresAt');

    if (userId && token && expiresAt) {
      if (!this.isTokenExpired(Number(expiresAt))) {
        return {
          userId,
          token,
          expiresAt: Number(expiresAt)
        };
      }
    }

    // Otherwise, token is expired or not available, so clear local storage
    this.removeAuth();
    return null;
  }

  removeAuth() {
    localStorage.removeItem('userId');
    localStorage.removeItem('token');
    localStorage.removeItem('expiresAt');
  }

  isTokenExpired(expiresAt: number): boolean {
    return new Date().getTime() > expiresAt;
  }

  isAuthenticated() {
    return this.getAuth() !== null;
  }
}