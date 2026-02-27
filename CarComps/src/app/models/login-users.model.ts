export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  result: {
    firstName: string;
    lastName: string;
    role: string;
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

export interface GetUserByIdResponse {}

export interface GetAddressByIdResponse {
  errors: string[];
  address: {
    lastName: string;
    country: string;
    zipCode: string;
    city: string;
    taxNumber: string;
    userId: number;
    firstName: string;
    createdAt: string;
    isDefault: boolean;
    street: string;
    company: string;
    id: number;
    updatedAt: string;
  };
  success: boolean;
  statusCode: number;
}
