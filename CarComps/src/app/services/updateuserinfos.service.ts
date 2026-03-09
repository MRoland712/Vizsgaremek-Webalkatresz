import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UpdateUserInfosRequest, UpdateUserInfosResponse } from '../models/updateuserinfos.model';

@Injectable({ providedIn: 'root' })
export class UpdateUserInfosService {
  private http = inject(HttpClient);
  private baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  updateUserInfos(
    email: string,
    body: UpdateUserInfosRequest,
  ): Observable<UpdateUserInfosResponse> {
    // PUT user/updateUser?email=...  +  token header
    const token = localStorage.getItem('jwt') ?? '';
    const headers = new HttpHeaders({ token });
    return this.http.put<UpdateUserInfosResponse>(`${this.baseUrl}user/updateUser`, body, {
      params: { email },
      headers,
    });
  }
}
