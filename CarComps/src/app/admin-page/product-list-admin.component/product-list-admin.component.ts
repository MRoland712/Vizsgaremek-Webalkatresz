import { Component, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';

import { DecimalPipe } from '@angular/common';
import { PartsModel } from '../../models/parts.model';
import { GetallpartsService } from '../../services/getallparts.service';
import { GetallpartimgagesService } from '../../services/getallpartimages.service';

// PartsModel + imageUrl összefűzve
export interface PartWithImage extends PartsModel {
  imageUrl?: string;
}

@Component({
  selector: 'app-product-list-admin',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './product-list-admin.component.html',
  styleUrl: './product-list-admin.component.css',
})
export class ProductListAdminComponent implements OnInit {
  private router = inject(Router);
  private partsService = inject(GetallpartsService);
  private imgsService = inject(GetallpartimgagesService);

  isLoading = signal(true);
  hasError = signal(false);
  allParts = signal<PartWithImage[]>([]);
  filtered = signal<PartWithImage[]>([]);
  selected = signal<PartWithImage | null>(null);

  searchTerm = signal('');
  filterStatus = signal('all');
  filterCategory = signal('all');
  categories = signal<string[]>([]);

  ngOnInit(): void {
    this.loadParts();
  }

  loadParts(): void {
    this.isLoading.set(true);
    this.hasError.set(false);

    forkJoin({
      parts: this.partsService.getAllParts(),
      images: this.imgsService.getAllPartImages(),
    }).subscribe({
      next: ({ parts, images }) => {
        const imageMap = new Map<number, string>();
        for (const img of images.partImages ?? []) {
          if (img.isPrimary || !imageMap.has(img.partId)) {
            const fixedUrl = img.url
              ?.replace('http://api.Carcomps.hu', 'https://api.carcomps.hu')
              ?.replace('http://api.carcomps.hu', 'https://api.carcomps.hu');
            imageMap.set(img.partId, fixedUrl ?? '');
          }
        }

        const partsWithImages: PartWithImage[] = (parts.parts ?? []).map((p) => ({
          ...p,
          imageUrl: imageMap.get(p.id) ?? undefined,
        }));

        this.allParts.set(partsWithImages);
        this.filtered.set(partsWithImages);

        const cats = [...new Set(partsWithImages.map((p) => p.category).filter(Boolean))].sort();
        this.categories.set(cats);

        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Betöltési hiba:', err);
        this.hasError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  applyFilters(search: string, status: string, category: string): void {
    this.searchTerm.set(search);
    this.filterStatus.set(status);
    this.filterCategory.set(category);

    let result = this.allParts();

    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(
        (p) =>
          p.name?.toLowerCase().includes(q) ||
          p.sku?.toLowerCase().includes(q) ||
          p.category?.toLowerCase().includes(q),
      );
    }

    if (status !== 'all') result = result.filter((p) => p.status === status);
    if (category !== 'all') result = result.filter((p) => p.category === category);

    this.filtered.set(result);
  }

  onSearch(event: Event): void {
    this.applyFilters(
      (event.target as HTMLInputElement).value,
      this.filterStatus(),
      this.filterCategory(),
    );
  }

  onStatusFilter(event: Event): void {
    this.applyFilters(
      this.searchTerm(),
      (event.target as HTMLSelectElement).value,
      this.filterCategory(),
    );
  }

  onCategoryFilter(event: Event): void {
    this.applyFilters(
      this.searchTerm(),
      this.filterStatus(),
      (event.target as HTMLSelectElement).value,
    );
  }

  openDetail(part: PartWithImage): void {
    this.selected.set(part);
  }
  closeDetail(): void {
    this.selected.set(null);
  }
  goBack(): void {
    this.router.navigate(['/admin']);
  }

  getStatusClass(status: string): string {
    return status === 'available' ? 'status-available' : 'status-unavailable';
  }
}
