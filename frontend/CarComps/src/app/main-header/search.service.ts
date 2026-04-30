import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { GetAllPartsWithImagesService } from '../services/getallpartswithimages.service';

export interface SearchResult {
  id: number;
  name: string;
  category: string;
  price: number;
  imageUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class SearchService {
  private partsService = inject(GetAllPartsWithImagesService);

  search(searchTerm: string): Observable<SearchResult[]> {
    return this.partsService.getAllPartsWithImages().pipe(
      map((res) => {
        const term = searchTerm.toLowerCase().trim();
        return (res.parts ?? [])
          .filter(
            (p) =>
              p.name?.toLowerCase().includes(term) ||
              p.category?.toLowerCase().includes(term) ||
              p.sku?.toLowerCase().includes(term),
          )
          .slice(0, 10)
          .map((p) => ({
            id: p.id,
            name: p.name,
            category: p.category,
            price: p.price,
            imageUrl: p.imageUrl,
          }));
      }),
    );
  }
}
