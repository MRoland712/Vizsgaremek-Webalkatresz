import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  CreateCartItemRequest,
  CreateCartItemResponse,
  GetCartItemsResponse,
  UpdateCartItemRequest,
  UpdateCartItemResponse,
  DeleteCartItemResponse,
} from '../models/cart-item.model';

@Injectable({ providedIn: 'root' })
export class CartItemsService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  private getHeaders(): { headers: HttpHeaders } {
    const token = localStorage.getItem('jwt') ?? '';
    console.log('🔑 JWT token:', token ? token.substring(0, 30) + '...' : 'ÜRES!');
    return { headers: new HttpHeaders({ 'Content-Type': 'application/json', token }) };
  }

  // POST cartItems/createCartItems
  createCartItem(body: CreateCartItemRequest): Observable<CreateCartItemResponse> {
    console.log('🛒 createCartItem body:', body);
    return this.http.post<CreateCartItemResponse>(
      `${this.baseUrl}cartItems/createCartItems`,
      body,
      this.getHeaders(),
    );
  }

  // GET cartItems/getCartItemsByUserId?userId=X
  getCartItemsByUserId(userId: number): Observable<GetCartItemsResponse> {
    const token = localStorage.getItem('jwt') ?? '';
    console.log(
      '🛒 getCartItems userId:',
      userId,
      '| token:',
      token ? token.substring(0, 30) + '...' : 'ÜRES!',
    );
    const headers = new HttpHeaders({ 'Content-Type': 'application/json', token });
    return this.http.get<GetCartItemsResponse>(
      `${this.baseUrl}cartItems/getCartItemsByUserId?userId=${userId}`,
      { headers },
    );
  }

  // PUT cartItems/updateCartItem?id=X
  updateCartItem(id: number, body: UpdateCartItemRequest): Observable<UpdateCartItemResponse> {
    return this.http.put<UpdateCartItemResponse>(
      `${this.baseUrl}cartItems/updateCartItem?id=${id}`,
      body,
      this.getHeaders(),
    );
  }

  // DELETE cartItems/softDeleteCartItem?id=X
  deleteCartItem(id: number): Observable<DeleteCartItemResponse> {
    return this.http.delete<DeleteCartItemResponse>(`${this.baseUrl}cartItems/softDeleteCartItem`, {
      params: { id },
      ...this.getHeaders(),
    });
  }
}
