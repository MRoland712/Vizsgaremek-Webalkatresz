import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SearchResult } from './main-header.component';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  // TODO: Cseréld le az API endpoint-odra
  private apiUrl = 'https://your-api-endpoint.com/api';

  constructor(private http: HttpClient) {}

  /**
   * Keresés az API-ban
   * @param searchTerm - A keresett kifejezés
   * @returns Observable SearchResult tömbbel
   */
  search(searchTerm: string): Observable<SearchResult[]> {
    // TODO: Módosítsd az endpoint-ot és a paramétereket az API specifikáció szerint
    return this.http.get<SearchResult[]>(`${this.apiUrl}/search`, {
      params: { q: searchTerm },
    });

    // Ha az API más formátumban ad vissza adatokat, használj map operátort:
    // return this.http.get<any>(`${this.apiUrl}/search`, {
    //   params: { q: searchTerm }
    // }).pipe(
    //   map(response => {
    //     // Alakítsd át az API válaszát SearchResult[] típusra
    //     return response.data.map((item: any) => ({
    //       id: item.id,
    //       name: item.productName,
    //       category: item.categoryName,
    //       price: item.price,
    //       imageUrl: item.image
    //     }));
    //   })
    // );
  }

  /**
   * Termék részletek lekérése ID alapján
   * @param id - Termék azonosító
   */
  getProductById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/products/${id}`);
  }
}
