export interface PartImagesModel {
  partId: number;
  isPrimary: boolean;
  id: number;
  url: string;
}
export interface GetAllPartImagesResponse {
  success: boolean;
  count: number;
  partImages: PartImagesModel[];
  statusCode: number;
}
