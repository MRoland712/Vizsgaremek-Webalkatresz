export interface CreateOrderRequest {
  userId: number;
}

export interface CreateOrderResponse {
  orderId: number;
  success: boolean;
  message: string;
  statusCode: number;
}

export interface Order {
  id: number;
  userId: number;
  status: string;
  isDeleted: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface GetAllOrdersByUserIdResponse {
  success: boolean;
  count: number;
  orders: Order[];
  statusCode: number;
}
