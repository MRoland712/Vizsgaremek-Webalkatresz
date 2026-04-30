import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { GetMostPurchasedPartsResponse } from '../models/getmostpurchasedparts.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class GetMostPurchasedPartsService {
  private httpClient = inject(HttpClient);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  getMostPurchasedParts(): Observable<GetMostPurchasedPartsResponse> {
    const url = this.baseUrl + 'Analitics/getMostPurchasedPart';
    return this.httpClient.get<GetMostPurchasedPartsResponse>(url);
  }
}
