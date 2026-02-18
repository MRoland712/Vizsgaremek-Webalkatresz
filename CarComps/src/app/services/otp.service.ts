import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { SendOTPResponse, VerifyOTPRequest, VerifyOTPResponse } from '../models/otp.model';

@Injectable({
  providedIn: 'root',
})
export class OtpService {
  private http = inject(HttpClient);

  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  sendOTP(email: string): Observable<SendOTPResponse> {
    const url = `${this.baseUrl}email/sendOTP`;

    const params = new HttpParams().set('email', email);

    console.log('🚀 sendOTP:');
    console.log('  POST', `${url}?email=${email}`);

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

  verifyOTP(body: VerifyOTPRequest): Observable<VerifyOTPResponse> {
    const url = `${this.baseUrl}OTP/verifyOTP`;

    return this.http.post<VerifyOTPResponse>(url, body);
  }
}
