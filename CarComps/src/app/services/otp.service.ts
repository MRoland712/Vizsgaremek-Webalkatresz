import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

export interface SendOTPResponse {
  success: boolean;
  message: string;
  statusCode: number;
}
export interface VerifyOTPRequest {
  email: string;
  OTP: number;
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
   * ⭐ SEND OTP
   * Method: POST
   * URL: email/sendOTP?email=vinrar712@gmail.com
   * Body: ÜRES (null)
   */
  sendOTP(email: string): Observable<SendOTPResponse> {
    const url = `${this.baseUrl}email/sendOTP`;

    // ⭐ Query param
    const params = new HttpParams().set('email', email);

    console.log('🚀 sendOTP:');
    console.log('  POST', `${url}?email=${email}`);

    // ⭐ POST + query param + üres body
    return this.http.post<SendOTPResponse>(url, null, { params }).pipe(
      tap((res) => {
        console.log('✅ sendOTP success:', res);
      }),
      catchError((err) => {
        console.error('❌ sendOTP error:', err);
        throw err;
      }),
    );
  }

  /**
   * ⭐ VERIFY OTP
   * Method: POST
   * URL: OTP/verifyOTP
   * Body: { "email": "...", "otp": "..." }
   */
  verifyOTP(body: VerifyOTPRequest): Observable<VerifyOTPResponse> {
    const url = `${this.baseUrl}OTP/verifyOTP`;

    return this.http.post<VerifyOTPResponse>(url, body);

    // ⭐ POST + JSON body
  }
}
