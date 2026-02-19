import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { JwtValidationResponse } from '../models/jtwvalidate.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class JWTValidateService {
  private httpClient = inject(HttpClient);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private readonly validateUrl = this.baseUrl + 'JWT/validateJWT';

  ValidateJWT(): Observable<JwtValidationResponse> {
    return this.httpClient.get<JwtValidationResponse>(this.validateUrl);
  }
}
