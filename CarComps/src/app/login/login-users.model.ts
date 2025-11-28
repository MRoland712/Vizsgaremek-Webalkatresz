export interface LoginRequest {
  email: string;
  password: string;
}
export interface LoginResponse {
  result: {
    Message?: string;
    JWTToken?: string;
  };
  status: string;
  statusCode: number;
}
export interface User {
  id: number;
  email: string;
  username: string;
}
