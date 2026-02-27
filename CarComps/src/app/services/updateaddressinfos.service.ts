import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  UpdateAddressInfosRequest,
  UpdateAddressInfosResponse,
  UpdateUserInfosRequest,
  UpdateUserInfosResponse,
} from '../models/updateuserinfos.model';

@Injectable({
  providedIn: 'root',
})
export class UpdateAddressInfosService {
  private httpClient = inject(HttpClient);
  private baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private updateUrl = this.baseUrl + 'updateAddress';
  updateAddressInfos(
    userId: number,
    body: UpdateAddressInfosRequest,
  ): Observable<UpdateAddressInfosResponse> {
    const params = new HttpParams().set('userId', userId);
    return this.httpClient.put<UpdateAddressInfosResponse>(this.updateUrl, { params, body });
  }
}
