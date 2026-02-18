import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface TFARequest {
  email: string;
}

export interface TFAResponseModel {
  QR: string;
  secretKey: string;
  recoveryCodes: string[];
}

export interface TFAResponse {
  result: TFAResponseModel; // ✅ objektum, nem tömb
  status: string;
  statusCode: number;
}

export interface TFAValidationRequest {
  code: string;
  email: string;
}

export interface TFAValidationResponse {
  result: string;
  statusCode: number;
  status: string;
}

@Injectable({
  providedIn: 'root',
})
export class TfaService {
  private http = inject(HttpClient);
  // ✅ CORS proxy - ugyanaz mint a login service-ben
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  // ⭐ token header - ugyanaz mint Postman-ben
  private getHeaders(): { headers: HttpHeaders } {
    const token = localStorage.getItem('jwt');
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        token: token ?? '',
      }),
    };
  }

  // ⭐ validateTFACode-hoz text/plain kell (Postman szerint)
  private getVerifyHeaders(): { headers: HttpHeaders } {
    const token = localStorage.getItem('jwt');
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        token: token ?? '',
      }),
    };
  }

  CreateUserTfa(email: TFARequest): Observable<TFAResponse> {
    return this.http.post<TFAResponse>(
      `${this.baseUrl}UserTwofa/createUserTwofa`,
      email,
      this.getHeaders(),
    );
  }

  verifyTfaCode(email: string, code: string): Observable<TFAValidationResponse> {
    return this.http.post<TFAValidationResponse>(
      `${this.baseUrl}TFA/validateTFACode`,
      { email, code },
      this.getVerifyHeaders(), // ⭐ text/plain
    );
  }

  disableTfa(email: string): Observable<any> {
    return this.http.post(`${this.baseUrl}UserTwofa/disable`, { email }, this.getHeaders());
  }
}
