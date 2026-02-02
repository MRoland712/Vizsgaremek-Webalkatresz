import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ProductCardComponent } from '../product-card/product-card.component';
import { GetallpartsService } from '../../../services/getallparts.service';
import { PartsModel } from '../../../models/parts.model';
import { PartImagesModel } from '../../../models/partimages.model';
import { GetallpartimgagesService } from '../../../services/getallpartimages.service';

@Component({
  selector: 'app-product-list',
  imports: [ProductCardComponent],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css',
})
export class ProductListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private productsService = inject(GetallpartsService);
  private partImagesService = inject(GetallpartimgagesService);

  parts: PartsModel[] = [];
  currentCategory: string = '';

  ngOnInit(): void {
    // Query param figyelése
    this.route.params.subscribe((params) => {
      this.currentCategory = params['category'] || '';
      console.log('📦 Kategória szűrő:', this.currentCategory);

      // Termékek betöltése KÉPEKKEL
      this.loadPartCategories();
    });
  }

  loadPartCategories() {
    // ⭐ Párhuzamos betöltés - forkJoin
    forkJoin({
      parts: this.productsService.getAllParts(),
      images: this.partImagesService.getAllPartImages(),
    }).subscribe({
      next: ({ parts, images }) => {
        console.log('✅ Parts betöltve:', parts.parts.length);
        console.log('✅ Images betöltve:', images.partImages.length);

        if (parts.success && images.success) {
          // Képek hozzárendelése
          const partsWithImages = this.assignImagesToParts(parts.parts, images.partImages);

          // Szűrés kategóriára
          if (this.currentCategory) {
            this.parts = partsWithImages.filter(
              (part) => part.category.toLowerCase() === this.currentCategory.toLowerCase(),
            );
            console.log(`✅ ${this.parts.length} termék szűrve (${this.currentCategory})`);
          } else {
            this.parts = partsWithImages;
            console.log(`✅ ${this.parts.length} termék (összes)`);
          }
        }
      },
      error: (err) => {
        console.error('❌ Hiba a betöltés során:', err);
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
