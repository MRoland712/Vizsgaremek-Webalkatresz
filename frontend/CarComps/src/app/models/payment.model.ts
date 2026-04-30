// export interface CreateOrderWithItemRequest {
//   userId: number;
//   partId: number;
//   quantity: number;
// }
// export interface CreateOrderWithItemResponse {
//   succes: boolean;
//   message: string;
//   statusCode: number;
// }

// export interface ProcessPaymentRequest {
//   orderId: number;
//   amount: number;
//   method: string;
// }

// export interface ProcessPaymentResponse {
//   errors: string[];
//   status: string;
//   statusCode: number;
// }

// export interface GetAllPaymentsResponse {}
export interface ProcessPaymentRequest {
  orderId: number;
  method: string; // 'cash' | 'paypal' | 'mastercard' | 'visa'
}

export interface ProcessPaymentResponse {
  amount: number;
  method: string;
  orderId: number;
  success: boolean;
  message: string;
  statusCode: number;
}
