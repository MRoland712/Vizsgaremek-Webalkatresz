import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CreateUserVehicleRequest,
  CreateUserVehicleResponse,
} from '../models/createuservehicle.model';

@Injectable({ providedIn: 'root' })
export class CreateUserVehicleService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt') ?? '';
    return new HttpHeaders({ 'Content-Type': 'application/json', token });
  }

  createUserVehicle(body: CreateUserVehicleRequest): Observable<CreateUserVehicleResponse> {
    return this.http.post<CreateUserVehicleResponse>(
      `${this.baseUrl}userVehicles/createUserVehicle`,
      body,
      { headers: this.getHeaders() },
    );
  }
}
