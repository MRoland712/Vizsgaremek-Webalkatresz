import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ProcessPaymentRequest, ProcessPaymentResponse } from '../models/payment.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  private getHeaders(): { headers: HttpHeaders } {
    const token = localStorage.getItem('jwt') ?? '';
    return { headers: new HttpHeaders({ 'Content-Type': 'application/json', token }) };
  }

  // POST payments/processPayment
  processPayment(body: ProcessPaymentRequest): Observable<ProcessPaymentResponse> {
    return this.http.post<ProcessPaymentResponse>(
      `${this.baseUrl}payments/processPayment`,
      body,
      this.getHeaders(),
    );
  }
}
