export interface UpdateUserInfosRequest {
  firstName?: string;
  lastName?: string;
  username?: string;
  email?: string;
  phone?: string;
  password?: string;
  currentPassword?: string;
  newPassword?: string;
}

export interface UpdateUserInfosResponse {
  status: string;
  statusCode: number;
}

export interface UpdateAddressInfosRequest {
  firstName?: string;
  lastName?: string;
  country?: string;
  city?: string;
  zipCode?: string;
  street?: string;
  taxNumber?: string;
  company?: string;
}

export interface UpdateAddressInfosResponse {
  success: boolean;
  message: string;
  statusCode: number;
}

export interface CreateAddressRequest {
  userId: number;
  firstName: string;
  lastName: string;
  company?: string;
  taxNumber?: string;
  country: string;
  city: string;
  zipCode: string;
  street: string;
  isDefault: boolean;
}

export interface CreateAddressResponse {
  success: boolean;
  message: string;
  statusCode: number;
}
