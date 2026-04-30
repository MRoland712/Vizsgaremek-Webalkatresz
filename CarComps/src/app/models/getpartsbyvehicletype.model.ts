export interface GetPartsByVehicleTypeResponse {
  success: boolean;
  parts: Part[];
  count: number;
  statusCode: number;
  errors: string[];
}
export interface Part {
  createdAt: string;
  price: string;
  manufacturerId: number;
  imageUrl: string;
  name: string;
  description: string;
  id: number;
  sku: string;
  category: string;
  stock: number;
  isActive: boolean;
  status: string;
  updatedAt: string;
}
