export interface CreateReviewRequest {
  userId: number;
  partId: number;
  ratingIN: number;
  commentIN: string;
}

export interface CreateReviewResponse {
  success: boolean;
  message: string;
  statusCode: number;
}

export interface GetReviewsByPartIdResponse {
  Reviews: ReviewModel[];
  success: boolean;
  count: number;
  statusCode: number;
}

export interface ReviewModel {
  createdAt: string;
  isDeleted: boolean;
  partId: number;
  rating: number;
  comment: string;
  id: number;
  userId: number;
}
