import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GetAllPartsWithImagesResponse } from '../models/getallpartswithimages.model';

@Injectable({ providedIn: 'root' })
export class GetAllPartsWithImagesService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  getAllPartsWithImages(): Observable<GetAllPartsWithImagesResponse> {
    return this.http.get<GetAllPartsWithImagesResponse>(
      `${this.baseUrl}parts/getAllPartsWithImages`,
    );
  }
}
