import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetAllPartImagesResponse } from '../models/partimages.model';

@Injectable({
  providedIn: 'root',
})
export class GetallpartimgagesService {
  private httpClient = inject(HttpClient);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private readonly partImagesUrl = this.baseUrl + 'partimages/getAllPartImages';
  getAllPartImages(): Observable<GetAllPartImagesResponse> {
    return this.httpClient.get<GetAllPartImagesResponse>(this.partImagesUrl);
  }
}
