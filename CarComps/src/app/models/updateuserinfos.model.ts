export interface UpdateUserInfosRequest {
  firstName: string;
  lastName: string;
  email: string;
  username: string;
  phone: string;
  isActive: boolean;
  isSubscribed: boolean;
  password: string;
  authSecret: string; //nem kell, csak a backend miatt van benne
  registrationToken: string; //nem kell, csak a backend miatt van benne
}
export interface UpdateUserInfosResponse {
  errors: string[];
  status: string;
  statusCode: number;
}

export interface UpdateAddressInfosRequest {
  firstName: string;
  lastName: string;
  company: string;
  taxNumber: string;
  country: string;
  city: string;
  zipCode: string;
  street: string;
  isDefault: boolean;
}

export interface UpdateAddressInfosResponse {
  errors: string[];
  success: boolean;
  message: string;
  statusCode: number;
}
