import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ProductCardComponent } from '../product-card/product-card.component';
import { GetallpartsService } from '../../../services/getallparts.service';
import { PartsModel } from '../../../models/parts.model';
import { PartImagesModel } from '../../../models/partimages.model';
import { GetallpartimgagesService } from '../../../services/getallpartimages.service';
import { BreadcrumbService } from '../../../services/breadcrumb.service';

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
  private breadcrumbService = inject(BreadcrumbService); // ⭐ INJECT

  parts: PartsModel[] = [];
  currentCategory: string = '';

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.currentCategory = params['category'] || '';
      console.log('📦 Kategória szűrő:', this.currentCategory);

      // ⭐ Kategória mentése breadcrumb service-be
      if (this.currentCategory) {
        this.breadcrumbService.setLastCategory(this.currentCategory);
        console.log('✅ Kategória mentve breadcrumb-ba:', this.currentCategory);
      }

      this.loadPartCategories();
    });
  }

  loadPartCategories() {
    forkJoin({
      parts: this.productsService.getAllParts(),
      images: this.partImagesService.getAllPartImages(),
    }).subscribe({
      next: ({ parts, images }) => {
        console.log('✅ Parts betöltve:', parts.parts.length);
        console.log('✅ Images betöltve:', images.partImages.length);

        if (parts.success && images.success) {
          const partsWithImages = this.assignImagesToParts(parts.parts, images.partImages);

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

  private assignImagesToParts(parts: PartsModel[], images: PartImagesModel[]): PartsModel[] {
    const imageMap = new Map<number, string>();

    // Primary képek
    images.forEach((image) => {
      if (image.isPrimary) {
        imageMap.set(image.partId, image.url);
      }
    });

    // Első kép ha nincs primary
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
}
