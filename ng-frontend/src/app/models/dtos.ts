// dtos.ts
export interface AuthDTO {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  expiresAt: number;
}