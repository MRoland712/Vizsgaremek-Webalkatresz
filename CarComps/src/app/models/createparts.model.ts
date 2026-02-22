export interface CreatePartsRequest {
  manufacturerId: number;
  sku: string;
  name: string;
  category: string;
  price: string;
  stock: number;
  status: string;
  isActive: boolean;
}
export interface CreatePartsResponse {
  errors: string[];
  success: boolean;
  message: string;
  statusCode: number;
}
