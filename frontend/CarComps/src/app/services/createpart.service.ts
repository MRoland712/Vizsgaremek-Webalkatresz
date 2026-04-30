import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreatePartsRequest, CreatePartsResponse } from '../models/createparts.model';

@Injectable({
  providedIn: 'root',
})
export class CreatePartService {
  private httpClient = inject(HttpClient);

  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt') ?? '';
    return new HttpHeaders({ token, 'Content-Type': 'application/json' });
  }

  createPart(body: CreatePartsRequest): Observable<CreatePartsResponse> {
    const url = `${this.baseUrl}parts/createParts`;
    return this.httpClient.post<CreatePartsResponse>(url, body, {
      headers: this.getAuthHeaders(),
    });
  }

  uploadPartImage(partId: number, file: File, isPrimary: boolean): Observable<any> {
    const url = `${this.baseUrl}partimages/uploadPartImage`;
    const token = localStorage.getItem('jwt') ?? '';

    // multipart/form-data — NE állítsunk Content-Type-ot, a browser maga teszi
    const headers = new HttpHeaders({ token });

    const formData = new FormData();
    formData.append('partId', String(partId));
    formData.append('isPrimary', String(isPrimary));
    formData.append('file', file);

    return this.httpClient.post<any>(url, formData, { headers });
  }
}
