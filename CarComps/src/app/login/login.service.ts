import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, tap, throwError } from 'rxjs';
import { LoginRequest, LoginResponse, User } from './login-users.model';
@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private httpClient = inject(HttpClient);

  private readonly baseUrl = 'http://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  private readonly loginUserUrl = this.baseUrl + 'user/loginUser';

  headers = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': 'http://localhost:4200',
    }),
  };

  login(body: LoginRequest): Observable<LoginResponse> {
    return this.httpClient.put<LoginResponse>(this.loginUserUrl, body, this.headers);
  }
}
