export interface CreateOrderWithItemRequest {
  userId: number;
  partId: number;
  quantity: number;
}
export interface CreateOrderWithItemResponse {
  succes: boolean;
  message: string;
  statusCode: number;
}

export interface ProcessPaymentRequest {
  orderId: number;
  amount: number;
  method: string;
}

export interface ProcessPaymentResponse {
  errors: string[];
  status: string;
  statusCode: number;
}

export interface GetAllPaymentsResponse {}
