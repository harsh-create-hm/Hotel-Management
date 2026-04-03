export interface User {
  id: number;
  name: string;
  email: string;
  role: 'USER' | 'ADMIN';
}

export interface AuthResponse {
  token: string;
  email: string;
  role: string;
  name: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}
