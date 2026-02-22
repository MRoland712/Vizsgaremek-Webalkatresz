export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  result: {
    firstName: string;
    lastName: string;
    role: string; // ← result-on BELÜL van
    phone: string;
    message?: string;
    JWTToken?: string;
    username: string;
  };
  status: string;
  statusCode: number;
}

export interface User {
  id: number;
  email: string;
  username: string;
}
