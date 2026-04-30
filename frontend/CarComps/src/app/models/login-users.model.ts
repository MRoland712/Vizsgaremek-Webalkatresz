export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  result: {
    JWTToken?: string;
    Message?: string;
    userId?: number;
    username?: string;
    firstName?: string;
    lastName?: string;
    phone?: string;
    role?: string;
  };
  status: string;
  statusCode: number;
}

export interface User {
  id: number;
  email: string;
  username: string;
}

export interface GetUserByIdResponse {
  result: {
    id: number;
    email: string;
    username: string;
    firstName: string;
    lastName: string;
    phone: string;
    role: string;
    isActive: boolean;
    isDeleted: boolean;
    isSubscribed: boolean;
    createdAt: string;
    lastLogin: string;
    guid: string;
    registrationToken: string;
    authSecret: string;
  };
  status: string;
  statusCode: number;
}

export interface GetAddressByIdResponse {
  address: {
    id: number;
    userId: number;
    firstName: string;
    lastName: string;
    country: string;
    zipCode: string;
    city: string;
    street: string;
    taxNumber: string;
    company: string;
    isDefault: boolean;
    createdAt: string;
    updatedAt: string;
  };
  success: boolean;
  statusCode: number;
}
