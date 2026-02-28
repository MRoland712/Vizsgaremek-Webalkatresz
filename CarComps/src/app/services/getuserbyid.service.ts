import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetUserByIdResponse } from '../models/login-users.model';

@Injectable({ providedIn: 'root' })
export class GetUserByIdService {
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private readonly http = inject(HttpClient);

  getUserById(userId: number): Observable<GetUserByIdResponse> {
    const token = localStorage.getItem('jwt') ?? '';
    const headers = new HttpHeaders({ token });
    return this.http.get<GetUserByIdResponse>(`${this.baseUrl}user/getUserById`, {
      params: { id: userId },
      headers,
    });
  }
}
