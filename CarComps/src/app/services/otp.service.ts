import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

export interface SendOTPResponse {
  success: boolean;
  message: string;
  statusCode: number;
}

export interface VerifyOTPResponse {
  success: boolean;
  message: string;
  verified: boolean;
  statusCode: number;
}

@Injectable({
  providedIn: 'root',
})
export class OtpService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'http://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  /**
   * ⭐ OTP küldése email-re
   * Method: POST
   * URL: baseUrl + email/sendOTP?email=user@example.com
   * FONTOS: Query param használata, NEM JSON body!
   */
  sendOTP(email: string): Observable<SendOTPResponse> {
    const url = `${this.baseUrl}email/sendOTP`;

    // ⭐ Query param hozzáadása
    const params = new HttpParams().set('email', email);

    console.log('🚀 OTP sendOTP hívás:');
    console.log('  Method: POST');
    console.log('  URL:', url);
    console.log('  Query Param:', `?email=${email}`);
    console.log('  Teljes URL:', `${url}?email=${email}`);

    // ⭐ POST kérés query param-mal (body ÜRES vagy null)
    return this.http.post<SendOTPResponse>(url, null, { params }).pipe(
      tap((res) => {
        console.log('✅ sendOTP response:', res);
      }),
      catchError((err) => {
        console.error('❌ sendOTP hiba:', err);
        console.error('  Status:', err.status);
        console.error('  URL:', err.url);
        console.error('  Error:', err.error);
        throw err;
      }),
    );
  }

  /**
   * ⭐ OTP verifikáció
   * Method: POST (valószínű)
   * URL: baseUrl + OTP/verifyOTP?email=...&otp=...
   */
  verifyOTP(email: string, otp: string): Observable<VerifyOTPResponse> {
    const url = `${this.baseUrl}OTP/verifyOTP`;

    // ⭐ Query params hozzáadása
    const params = new HttpParams().set('email', email).set('otp', otp);

    console.log('🚀 OTP verifyOTP hívás:');
    console.log('  Method: POST');
    console.log('  URL:', url);
    console.log('  Query Params:', `?email=${email}&otp=${otp}`);
    console.log('  Teljes URL:', `${url}?email=${email}&otp=${otp}`);

    // ⭐ POST kérés query param-mal
    return this.http.post<VerifyOTPResponse>(url, null, { params }).pipe(
      tap((res) => {
        console.log('✅ verifyOTP response:', res);
      }),
      catchError((err) => {
        console.error('❌ verifyOTP hiba:', err);
        console.error('  Status:', err.status);
        console.error('  URL:', err.url);
        console.error('  Error:', err.error);
        throw err;
      }),
    );
  }
}
//TODO : QUERY PARAM
