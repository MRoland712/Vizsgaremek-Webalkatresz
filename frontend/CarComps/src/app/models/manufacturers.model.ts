export interface ManufacturersModel {
  id: number;
  country: string;
  name: string;
}
export interface ManufacturersResponse {
  success: boolean;
  Manufacturers: ManufacturersModel[];
  statuscode: number;
}
