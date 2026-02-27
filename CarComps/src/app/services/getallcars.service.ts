import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetAllCarsResponse } from '../models/cars.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class GetAllCarsService {
  private httpClient = inject(HttpClient);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private readonly getAllCarsUrl = this.baseUrl + 'cars/getAllCars';

  getAllCars(): Observable<GetAllCarsResponse> {
    return this.httpClient.get<GetAllCarsResponse>(this.getAllCarsUrl);
  }
}
