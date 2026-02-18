import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ProductCardComponent } from '../product-card/product-card.component';
import { GetallpartsService } from '../../../services/getallparts.service';
import { GetallpartimgagesService } from '../../../services/getallpartimages.service';
import { BreadcrumbService } from '../../../services/breadcrumb.service';
import { FilterService } from '../../../services/filter.service';
import { PartsModel } from '../../../models/parts.model';
import { PartImagesModel } from '../../../models/partimages.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [ProductCardComponent],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'],
})
export class ProductListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private productsService = inject(GetallpartsService);
  private partImagesService = inject(GetallpartimgagesService);
  private breadcrumbService = inject(BreadcrumbService);
  private filterService = inject(FilterService);

  allProducts = signal<PartsModel[]>([]);
  isLoading = signal(true);
  currentCategory = signal<string>('');

  // ⭐ Filter prioritás: ha van aktív filter, az URL kategória nem számít
  filteredProducts = computed(() => {
    let products = this.allProducts();

    // Ha van aktív FilterService szűrő, az felülírja az URL kategóriát
    if (this.filterService.hasActiveFilters()) {
      products = products.filter((p) =>
        this.filterService.matchesFilters({
          category: p.category,
          manufacturerId: p.manufacturerId,
        }),
      );
    } else {
      // Csak akkor használja az URL kategóriát, ha NINCS aktív filter
      const category = this.currentCategory();
      if (category) {
        products = products.filter((p) => p.category.toLowerCase() === category.toLowerCase());
      }
    }

    return products;
  });

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const category = params['category'] || '';

      // ⭐ Ha változott a kategória, töröld a filtereket
      if (category !== this.currentCategory()) {
        this.filterService.clearFilters();
      }

      this.currentCategory.set(category);

      if (category) {
        this.breadcrumbService.setLastCategory(category);
        console.log('✅ Kategória:', category);
      }

      this.loadPartCategories();
    });
  }

  loadPartCategories() {
    this.isLoading.set(true);

    forkJoin({
      parts: this.productsService.getAllParts(),
      images: this.partImagesService.getAllPartImages(),
    }).subscribe({
      next: ({ parts, images }) => {
        console.log('✅ Parts betöltve:', parts.parts.length);
        console.log('✅ Images betöltve:', images.partImages.length);

        if (parts.success && images.success) {
          const partsWithImages = this.assignImagesToParts(parts.parts, images.partImages);
          this.allProducts.set(partsWithImages);
          console.log('✅ Termékek képekkel:', partsWithImages.length);
        }

        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('❌ Betöltési hiba:', err);
        this.isLoading.set(false);
      },
    });
  }

  private assignImagesToParts(parts: PartsModel[], images: PartImagesModel[]): PartsModel[] {
    const imageMap = new Map<number, string>();

    console.log('🖼️ Image assignment - total images:', images.length);

    // Primary képek
    images.forEach((image) => {
      if (image.isPrimary) {
        imageMap.set(image.partId, image.url);
        console.log(`✅ Primary image for part ${image.partId}:`, image.url);
      }
    });

    // Első kép ha nincs primary
    images.forEach((image) => {
      if (!imageMap.has(image.partId)) {
        imageMap.set(image.partId, image.url);
        console.log(`📌 First image for part ${image.partId}:`, image.url);
      }
    });

    const result = parts.map((part) => ({
      ...part,
      imageUrl: imageMap.get(part.id) || 'assets/placeholder.jpg',
    }));

    console.log('🖼️ Parts with images:', result.length);
    console.log('🖼️ Sample:', result[0]);

    return result;
  }
}
