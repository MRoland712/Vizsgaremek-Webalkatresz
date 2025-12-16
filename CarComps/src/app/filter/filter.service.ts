import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { getPartsById } from '../models/parts.model';

@Injectable({
  providedIn: 'root',
})
export class FilterService {
  private httpClient = inject(HttpClient);
  private readonly baseUrl = 'http://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private readonly partsUrl = this.baseUrl + 'parts/getAllParts';
  getAllParts(body: any): Observable<getPartsById> {
    return this.httpClient.get<getPartsById>(this.partsUrl);
  }
}
