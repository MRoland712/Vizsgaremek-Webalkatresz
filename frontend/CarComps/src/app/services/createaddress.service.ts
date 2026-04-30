import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateAddressRequest, CreateAddressResponse } from '../models/updateuserinfos.model';

@Injectable({ providedIn: 'root' })
export class CreateAddressService {
  private http = inject(HttpClient);
  private baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  createAddress(body: CreateAddressRequest): Observable<CreateAddressResponse> {
    // POST addresses/createAddress  +  token header
    const token = localStorage.getItem('jwt') ?? '';
    const headers = new HttpHeaders({ token });
    return this.http.post<CreateAddressResponse>(`${this.baseUrl}addresses/createAddress`, body, {
      headers,
    });
  }
}
