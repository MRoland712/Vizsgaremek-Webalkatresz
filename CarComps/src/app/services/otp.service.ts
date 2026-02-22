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

  // Ugyanaz a proxy mint LoginService-ben — ez az ami tényleg működik
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  sendOTP(email: string): Observable<SendOTPResponse> {
    const url = `${this.baseUrl}email/sendOTP`;
    const params = new HttpParams().set('email', email);
    // Postman 4. kép: Content-Type application/json van, null body

    console.log('🚀 sendOTP:', url);

    return this.http.post<SendOTPResponse>(url, null, { params }).pipe(
      tap((res) => console.log('✅ sendOTP success:', res)),
      catchError((err) => {
        console.error('❌ sendOTP error:', err);
        throw err;
      }),
    );
  }

  verifyOTP(body: VerifyOTPRequest): Observable<VerifyOTPResponse> {
    const url = `${this.baseUrl}OTP/verifyOTP`;
    // Postman 5. kép: nincs Content-Type header — Angular alapból JSON-t küld body esetén
    return this.http.post<VerifyOTPResponse>(url, body);
  }
}
