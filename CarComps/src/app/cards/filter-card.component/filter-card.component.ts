import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { GetallpartsService } from '../../services/getallparts.service';
import { PartsModel } from '../../models/parts.model';

@Component({
  selector: 'app-filter-card',
  imports: [],
  templateUrl: './filter-card.component.html',
  styleUrl: './filter-card.component.css',
})
export class CategoryCardComponent implements OnInit {
  private partsService = inject(GetallpartsService);

  // Signals
  allParts = signal<PartsModel[]>([]);
  isLoading = signal<boolean>(false);
  error = signal<string | null>(null);

  // ✅ Computed - EGYEDI kategóriák Map-pel
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
          // Új kategória
          categoryMap.set(part.category, {
            name: part.category,
            count: 1,
            imageUrl: `assets/categories/${part.category.toLowerCase()}.jpg`,
            categoryUrl: `/products?category=${part.category}`,
          });
        }
      }
    });

    // Map → Array konverzió
    return Array.from(categoryMap.values());
  });

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories() {
    this.isLoading.set(true);
    this.error.set(null);

    this.partsService.getAllParts().subscribe({
      next: (response) => {
        if (response.success) {
          // ✅ Teljes parts array mentése
          this.allParts.set(response.parts);
        } else {
          this.error.set('Nem sikerült betölteni a termékeket');
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        this.error.set('Hiba történt a betöltés során');
        this.isLoading.set(false);
      },
    });
  }
}
