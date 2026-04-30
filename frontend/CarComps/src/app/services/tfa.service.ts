import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  TFARequest,
  TFAResponse,
  TFAValidationResponse,
  TFARecordResponse,
} from '../models/TFA.model';

@Injectable({ providedIn: 'root' })
export class TfaService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  private getHeaders(): { headers: HttpHeaders } {
    const token = localStorage.getItem('jwt') ?? '';
    return { headers: new HttpHeaders({ 'Content-Type': 'application/json', token }) };
  }

  CreateUserTfa(body: TFARequest): Observable<TFAResponse> {
    return this.http.post<TFAResponse>(
      `${this.baseUrl}UserTwofa/createUserTwofa`,
      body,
      this.getHeaders(),
    );
  }

  verifyTfaCode(email: string, code: string): Observable<TFAValidationResponse> {
    return this.http.post<TFAValidationResponse>(
      `${this.baseUrl}TFA/validateTFACode`,
      { email, code },
      this.getHeaders(),
    );
  }

  // ÚJ: TFA rekord lekérdezése userId alapján
  getUserTwofa(userId: number): Observable<TFARecordResponse> {
    return this.http.get<TFARecordResponse>(
      `${this.baseUrl}UserTwofa/getUserTwofaByUserId?userId=${userId}`,
      this.getHeaders(),
    );
  }

  // ÚJ: TFA törlése a rekord id-jával
  disableTfa(tfaRecordId: number): Observable<any> {
    return this.http.delete(
      `${this.baseUrl}UserTwofa/softDeleteUserTwofa?id=${tfaRecordId}`,
      this.getHeaders(),
    );
  }
}
