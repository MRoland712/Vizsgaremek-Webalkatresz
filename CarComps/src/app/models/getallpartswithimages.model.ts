export interface PartWithImagesModel {
  id: number;
  name: string;
  sku: string;
  category: string;
  price: number;
  stock: number;
  isActive: boolean;
  status: string;
  manufacturerId: number;
  imageUrl: string; // közvetlenül a response-ban jön
  createdAt: string;
  updatedAt: string;
}

export interface GetAllPartsWithImagesResponse {
  success: boolean;
  parts: PartWithImagesModel[];
  count: number;
  statusCode: number;
}
