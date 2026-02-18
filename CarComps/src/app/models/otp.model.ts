export interface SendOTPResponse {
  success: boolean;
  message: string;
  statusCode: number;
}
export interface VerifyOTPRequest {
  email: string;
  OTP: number;
}
export interface VerifyOTPResponse {
  success: boolean;
  message: string;
  verified: boolean;
  statusCode: number;
}
