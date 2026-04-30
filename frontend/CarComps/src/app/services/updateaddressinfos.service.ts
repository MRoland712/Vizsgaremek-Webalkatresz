import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  UpdateAddressInfosRequest,
  UpdateAddressInfosResponse,
} from '../models/updateuserinfos.model';

@Injectable({ providedIn: 'root' })
export class UpdateAddressInfosService {
  private http = inject(HttpClient);
  private baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  updateAddressInfos(
    addressId: number,
    body: UpdateAddressInfosRequest,
  ): Observable<UpdateAddressInfosResponse> {
    // PUT addresses/updateAddress?id=6  +  token header
    const token = localStorage.getItem('jwt') ?? '';
    const headers = new HttpHeaders({ token });
    return this.http.put<UpdateAddressInfosResponse>(
      `${this.baseUrl}addresses/updateAddress`,
      body,
      { params: { id: addressId }, headers },
    );
  }
}
