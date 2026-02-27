import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UpdateUserInfosRequest, UpdateUserInfosResponse } from '../models/updateuserinfos.model';

@Injectable({
  providedIn: 'root',
})
export class UpdateUserInfosService {
  private httpClient = inject(HttpClient);
  private baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private updateUrl = this.baseUrl + 'user/updateUser';
  updateUserInfos(
    userId: number,
    body: UpdateUserInfosRequest,
  ): Observable<UpdateUserInfosResponse> {
    const params = new HttpParams().set('userId', userId);
    return this.httpClient.put<UpdateUserInfosResponse>(this.updateUrl, { params, body });
  }
}
