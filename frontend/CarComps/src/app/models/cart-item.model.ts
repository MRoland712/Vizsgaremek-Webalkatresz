export interface CreateCartItemRequest {
  userId: number;
  partId: number;
  quantity: number;
}

export interface CreateCartItemResponse {
  success: boolean;
  message: string;
  statusCode: number;
}

export interface CartItemModel {
  id: number;
  userId: number;
  partId: number;
  quantity: number;
  isDeleted: boolean;
  partName?: string; // backend adja vissza
  partPrice?: number; // backend adja vissza
  addedAt?: string;
}

export interface GetCartItemsResponse {
  success: boolean;
  count?: number;
  cartItems: CartItemModel[];
  statusCode?: number;
}

export interface UpdateCartItemRequest {
  id: number;
  userId: number;
  partId: number;
  quantity: number;
  isDeleted: number;
}

export interface UpdateCartItemResponse {
  success: boolean;
  message: string;
  statusCode: number;
}

export interface DeleteCartItemResponse {
  success: boolean;
  message: string;
  statusCode: number;
}
