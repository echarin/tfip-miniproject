export interface SignupDTO {
  email: string;
  password: string;
}

export interface SignupResponse {
  token: string;
}

export interface LoginDTO {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}