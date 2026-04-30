import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  CreatePasswordResetRequest,
  CreatePasswordResetResponse,
} from '../models/passwordreset.model';

@Injectable({
  providedIn: 'root',
})
export class PasswordResetService {
  private httpClient = inject(HttpClient);

  private baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private passwordResetUrl = `${this.baseUrl}passwordReset/createPasswordReset`;

  createPasswordReset(body: CreatePasswordResetRequest): Observable<CreatePasswordResetResponse> {
    return this.httpClient.post<CreatePasswordResetResponse>(this.passwordResetUrl, body);
  }
}
