export interface RegisterBody {
  email: string;
  password: string;
  username: string;
  firstName: string;
  lastName: string;
  phone: string;
}
export interface RegisterResponse {
  errors: [];
  status: string;
  statusCode: number;
}
