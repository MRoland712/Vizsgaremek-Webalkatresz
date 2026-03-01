import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TFARequest, TFAResponse, TFAValidationResponse } from '../models/TFA.model';

@Injectable({ providedIn: 'root' })
export class TfaService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  private getHeaders(): { headers: HttpHeaders } {
    const token = localStorage.getItem('jwt') ?? '';
    return { headers: new HttpHeaders({ 'Content-Type': 'application/json', token }) };
  }

  // POST UserTwofa/createUserTwofa — QR kód generálása
  CreateUserTfa(body: TFARequest): Observable<TFAResponse> {
    return this.http.post<TFAResponse>(
      `${this.baseUrl}UserTwofa/createUserTwofa`,
      body,
      this.getHeaders(),
    );
  }

  // POST TFA/validateTFACode — kód ellenőrzése
  verifyTfaCode(email: string, code: string): Observable<TFAValidationResponse> {
    return this.http.post<TFAValidationResponse>(
      `${this.baseUrl}TFA/validateTFACode`,
      { email, code },
      this.getHeaders(),
    );
  }

  disableTfa(email: string): Observable<any> {
    return this.http.post(`${this.baseUrl}TFA/disable`, { email }, this.getHeaders());
  }
}
