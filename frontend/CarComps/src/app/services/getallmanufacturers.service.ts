import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ManufacturersResponse } from '../models/manufacturers.model';

@Injectable({
  providedIn: 'root',
})
export class GetallmanufacturersService {
  private httpClient = inject(HttpClient);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private readonly manufactsUrl = this.baseUrl + 'manufacturers/getAllManufacturers';
  getAllManufacturers(): Observable<ManufacturersResponse> {
    return this.httpClient.get<ManufacturersResponse>(this.manufactsUrl);
  }
}
