import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { GetallpartsService } from '../../services/getallparts.service';
import { PartsModel } from '../../models/parts.model';
import { PartImagesModel } from '../../models/partimages.model';
import { GetallpartimgagesService } from '../../services/getallpartimages.service';

@Component({
  selector: 'app-filter-card',
  imports: [RouterLink],
  templateUrl: './filter-card.component.html',
  styleUrl: './filter-card.component.css',
})
export class CategoryCardComponent implements OnInit {
  private partsService = inject(GetallpartsService);
  private partImagesService = inject(GetallpartimgagesService);
  private router = inject(Router);

  allParts = signal<PartsModel[]>([]);
  isLoading = signal<boolean>(false);
  error = signal<string | null>(null);

  categories = computed(() => {
    const parts = this.allParts();
    const categoryMap = new Map<
      string,
      {
        name: string;
        count: number;
        imageUrl: string;
        categoryUrl: string;
      }
    >();

    parts.forEach((part) => {
      if (part.category) {
        if (categoryMap.has(part.category)) {
          const existing = categoryMap.get(part.category)!;
          existing.count++;
        } else {
          // ⭐ Normalizált URL
          const normalizedUrl = this.normalizeCategory(part.category);

          categoryMap.set(part.category, {
            name: part.category,
            count: 1,
            imageUrl: part.imageUrl || 'assets/placeholder.jpg',
            categoryUrl: normalizedUrl,
          });
        }
      }
    });
    return Array.from(categoryMap.values());
  });

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories() {
    this.isLoading.set(true);
    this.error.set(null);

    forkJoin({
      parts: this.partsService.getAllParts(),
      images: this.partImagesService.getAllPartImages(),
    }).subscribe({
      next: ({ parts, images }) => {
        if (parts.success && images.success) {
          const partsWithImages = this.assignImagesToParts(parts.parts, images.partImages);
          this.allParts.set(partsWithImages);
        } else {
          this.error.set('Nem sikerült betölteni az adatokat');
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('❌ Hiba:', err);
        this.error.set('Hiba történt');
        this.isLoading.set(false);
      },
    });
  }

  private assignImagesToParts(parts: PartsModel[], images: PartImagesModel[]): PartsModel[] {
    const imageMap = new Map<number, string>();

    images.forEach((image) => {
      if (image.isPrimary) {
        imageMap.set(image.partId, image.url);
      }
    });

    images.forEach((image) => {
      if (!imageMap.has(image.partId)) {
        imageMap.set(image.partId, image.url);
      }
    });

    return parts.map((part) => ({
      ...part,
      imageUrl: imageMap.get(part.id) || 'assets/placeholder.jpg',
    }));
  }

  // ⭐ UGYANAZ a normalizálás mint Filter component-ben
  private normalizeCategory(category: string): string {
    return category
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/\s+/g, '-');
  }
}
