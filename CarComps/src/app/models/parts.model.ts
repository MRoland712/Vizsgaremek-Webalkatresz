export interface PartsModel {
  createdAt: string;
  price: number;
  manufacturerId: number;
  name: string;
  id: number;
  sku: string;
  category: string;
  stock: number;
  isActive: boolean;
  status: string;
  updatedAt: string;
}

export interface dummyParts {
  id: number;
  name: string;
}

export interface GetAllPartsResponse {
  success: boolean;
  parts: PartsModel[];
}
