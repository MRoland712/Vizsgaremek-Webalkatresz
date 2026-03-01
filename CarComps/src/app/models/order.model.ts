export interface CreateOrderRequest {
  userId: number;
}

export interface CreateOrderResponse {
  orderId: number;
  success: boolean;
  message: string;
  statusCode: number;
}
