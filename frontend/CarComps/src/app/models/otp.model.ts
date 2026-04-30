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
  result: string;
  status: string;
  statusCode: number;
}
