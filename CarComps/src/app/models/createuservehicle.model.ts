export interface CreateUserVehicleRequest {
  vehicleType: string; // 'car' | 'motor' | 'truck'
  vehicleId: number;
  year: number;
  userId: number;
}

export interface CreateUserVehicleResponse {
  success: boolean;
  message: string;
  statusCode: number;
}
