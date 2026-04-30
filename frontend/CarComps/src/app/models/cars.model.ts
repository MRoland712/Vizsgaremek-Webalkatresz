export interface CreateCarsRequest {
  brand: string;
  model: string;
  yearFrom: number;
  yearTo: number;
}
export interface CreateCarsResponse {
  success: boolean;
  message: string;
  statusCode: number;
}

export interface GetAllCarsModel {
  Brand: string;
  createdAt: string;
  YearTo: number;
  isDeleted: boolean;
  Model: string;
  id: number;
  YearFrom: number;
  updatedAt: string;
}

export interface GetAllCarsResponse {
  cars: GetAllCarsModel[];
}
