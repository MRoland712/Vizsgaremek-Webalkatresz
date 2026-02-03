import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetAllPartsResponse } from '../models/parts.model';

@Injectable({
  providedIn: 'root',
})
export class GetallpartsService {
  private httpClient = inject(HttpClient);
  private readonly baseUrl = 'http://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private readonly partsUrl = this.baseUrl + 'parts/getAllParts';
  getAllParts(): Observable<GetAllPartsResponse> {
    return this.httpClient.get<GetAllPartsResponse>(this.partsUrl);
  }
}
