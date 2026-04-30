export interface CreatePasswordResetRequest {
  email: string;
}

export interface CreatePasswordResetResponse {
  success: boolean;
  message: string;
  statusCode: number;
}
