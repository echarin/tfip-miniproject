// dtos.ts
export interface AuthRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  userId: string;
  token: string;
  expiresAt: number;
}