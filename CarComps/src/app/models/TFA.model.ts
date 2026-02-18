export interface TFARequest {
  email: string;
}
export interface TFAResponseModel {
  QR: string;
  secretKey: string;
  recoveryCodes: string[];
}
export interface TFAResponse {
  result: TFAResponseModel; // objektum, nem tömb!
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
