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

  // Signals
  allParts = signal<PartsModel[]>([]);
  isLoading = signal<boolean>(false);
  error = signal<string | null>(null);

  // ✅ Computed - EGYEDI kategóriák + KÉPEK
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

    // Egyedi kategóriák gyűjtése
    parts.forEach((part) => {
      if (part.category) {
        if (categoryMap.has(part.category)) {
          // Létező kategória - növeljük a számot
          const existing = categoryMap.get(part.category)!;
          existing.count++;
        } else {
          // ⭐ Új kategória - KÉP a part-ból
          categoryMap.set(part.category, {
            name: part.category,
            count: 1,
            // ⭐ ImageUrl a part-ból (már hozzá van rendelve)
            imageUrl: part.imageUrl || 'assets/placeholder.jpg',
            categoryUrl: `/products/${part.category}`,
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

    // ⭐ Párhuzamos betöltés - forkJoin
    forkJoin({
      parts: this.partsService.getAllParts(),
      images: this.partImagesService.getAllPartImages(),
    }).subscribe({
      next: ({ parts, images }) => {
        console.log('✅ Parts betöltve:', parts.parts.length);
        console.log('✅ Images betöltve:', images.partImages.length);

        if (parts.success && images.success) {
          // ⭐ Képek hozzárendelése part ID alapján
          const partsWithImages = this.assignImagesToParts(parts.parts, images.partImages);

          this.allParts.set(partsWithImages);
          console.log('✅ Parts képekkel:', partsWithImages.slice(0, 3));
        } else {
          this.error.set('Nem sikerült betölteni az adatokat');
        }

        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('❌ Hiba a betöltés során:', err);
        this.error.set('Hiba történt a betöltés során');
        this.isLoading.set(false);
      },
    });
  }

  /**
   * ⭐ Képek hozzárendelése part ID alapján
   */
  private assignImagesToParts(parts: PartsModel[], images: PartImagesModel[]): PartsModel[] {
    // Kép Map létrehozása gyors kereséshez
    const imageMap = new Map<number, string>();

    // Primary képek Map-be
    images.forEach((image) => {
      if (image.isPrimary) {
        imageMap.set(image.partId, image.url);
      }
    });

    // Ha nincs primary, akkor első kép
    images.forEach((image) => {
      if (!imageMap.has(image.partId)) {
        imageMap.set(image.partId, image.url);
      }
    });

    // Parts + imageUrl
    return parts.map((part) => ({
      ...part,
      imageUrl: imageMap.get(part.id) || 'assets/placeholder.jpg',
    }));
  }
}
