import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateCarsRequest, CreateCarsResponse } from '../models/cars.model';

@Injectable({
  providedIn: 'root',
})
export class CreateCarsService {
  private httpClient = inject(HttpClient);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private readonly CreateCarsUrl = this.baseUrl + 'cars/createCars';
  CreateCars(body: CreateCarsRequest): Observable<CreateCarsResponse> {
    return this.httpClient.post<CreateCarsResponse>(this.CreateCarsUrl, body);
  }
}
