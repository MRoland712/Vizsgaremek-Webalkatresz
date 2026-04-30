export interface TFARequest {
  email: string;
}

export interface TFAResponseModel {
  QR: string;
  secretKey: string;
  recoveryCodes: string[];
}

export interface TFAResponse {
  result: TFAResponseModel;
  status: string;
  statusCode: number;
}

export interface TFAValidationRequest {
  code: string;
  email: string;
}

export interface TFAValidationResponse {
  result: string;
  statusCode: number;
  status: string;
}

// ÚJ: getUserTwofaByUserId response
export interface TFARecord {
  id: number;
  userId: number;
  TFASecret: string;
  TFAEnabled: boolean;
  isDeleted: boolean;
  recoveryCodes: string[];
  createdAt: string;
  updatedAt: string;
}

export interface TFARecordResponse {
  result: TFARecord;
  status: string;
  statusCode: number;
}
