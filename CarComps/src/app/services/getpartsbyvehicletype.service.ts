import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { GetPartsByVehicleTypeResponse } from '../models/getpartsbyvehicletype.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class GetPartsByVehicleTypeService {
  private baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private httpClient = inject(HttpClient);

  headers = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };

  getPartsByVehicleType(body: any): Observable<GetPartsByVehicleTypeResponse> {
    const params = new HttpParams().set('vehicleType', body.vehicleType);
    const url = this.baseUrl + 'parts/getPartsByVehicleType';

    return this.httpClient.get<GetPartsByVehicleTypeResponse>(url, {
      params,
      headers: this.headers.headers,
    });
  }
}
