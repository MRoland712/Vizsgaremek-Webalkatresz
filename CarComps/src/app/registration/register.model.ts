export interface RegisterBody {
  email: string;
  password: string;
  username: string;
  firstName: string;
  lastName: string;
  phone: string;
}
export interface RegisterResponse {
  result: {
    message?: string;
    JWTToken?: string;
  };
  status: string;
  statusCode: number;
}
