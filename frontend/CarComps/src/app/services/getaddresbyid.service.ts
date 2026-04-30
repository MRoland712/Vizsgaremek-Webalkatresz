import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetAddressByIdResponse } from '../models/login-users.model';

@Injectable({ providedIn: 'root' })
export class GetAddressByIdService {
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private readonly http = inject(HttpClient);

  getAddressById(userId: number): Observable<GetAddressByIdResponse> {
    const token = localStorage.getItem('jwt') ?? '';
    const headers = new HttpHeaders({ token });
    return this.http.get<GetAddressByIdResponse>(`${this.baseUrl}addresses/getAddressById`, {
      params: { id: userId },
      headers,
    });
  }
}
