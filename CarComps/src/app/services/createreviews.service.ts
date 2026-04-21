import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import {
  CreateReviewRequest,
  CreateReviewResponse,
  GetReviewsByPartIdResponse,
} from '../models/createreviews.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CreateReviewsService {
  private httpClient = inject(HttpClient);

  private baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  createReview(body: CreateReviewRequest): Observable<CreateReviewResponse> {
    const token = localStorage.getItem('jwt') ?? '';

    return this.httpClient.post<CreateReviewResponse>(
      `${this.baseUrl}reviews/createReviews`,
      body,
      {},
    );
  }

  getReviewsByPartId(partId: number): Observable<GetReviewsByPartIdResponse> {
    return this.httpClient.get<GetReviewsByPartIdResponse>(
      `${this.baseUrl}reviews/getReviewsByPartId`,
      {
        params: { partId: partId.toString() },
      },
    );
  }
}
