export interface GetMostPurchasedPartsResponse {
  result: GetMostPurchasedPart[];
  status: string;
  statusCode: number;
}

export interface GetMostPurchasedPart {
  quantity: number;
  partName: string;
}
