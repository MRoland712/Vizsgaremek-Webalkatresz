import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateOrderRequest, CreateOrderResponse } from '../models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  private getHeaders(): { headers: HttpHeaders } {
    const token = localStorage.getItem('jwt') ?? '';
    return { headers: new HttpHeaders({ 'Content-Type': 'application/json', token }) };
  }

  // POST orders/createOrderFromCart
  createOrderFromCart(body: CreateOrderRequest): Observable<CreateOrderResponse> {
    return this.http.post<CreateOrderResponse>(
      `${this.baseUrl}orders/createOrderFromCart`,
      body,
      this.getHeaders(),
    );
  }
}
