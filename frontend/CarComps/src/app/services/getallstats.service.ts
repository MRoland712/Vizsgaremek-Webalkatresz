import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { GetAllStatsResponse } from '../models/getallstats.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class GetAllStatsService {
  private httpClient = inject(HttpClient);
  private authService = inject(AuthService);

  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt') ?? '';
    return new HttpHeaders({ token });
  }

  getAllStats(days: string): Observable<GetAllStatsResponse> {
    // Admin ellenőrzés - csak admin JWT-vel lehet lekérni
    if (!this.authService.isAdmin()) {
      console.warn('⛔ getAllStats: nem admin felhasználó, a kérés blokkolva.');
      return throwError(() => new Error('UNAUTHORIZED: Admin jogosultság szükséges.'));
    }

    const url = this.baseUrl + 'Analitics/getAllStat';
    const params = new HttpParams().set('days', days);
    const headers = this.getAuthHeaders();

    return this.httpClient.get<GetAllStatsResponse>(url, { headers, params });
  }
}
