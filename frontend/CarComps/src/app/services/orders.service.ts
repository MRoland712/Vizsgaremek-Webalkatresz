import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  CreateOrderRequest,
  CreateOrderResponse,
  GetAllOrdersByUserIdResponse,
} from '../models/order.model';

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

  // GET orders/getOrdersByUserId?id=17
  getOrderByUserId(userId: number): Observable<GetAllOrdersByUserIdResponse> {
    const params = new HttpParams().set('id', userId);
    return this.http.get<GetAllOrdersByUserIdResponse>(`${this.baseUrl}orders/getOrdersByUserId`, {
      ...this.getHeaders(),
      params,
    });
  }
}
