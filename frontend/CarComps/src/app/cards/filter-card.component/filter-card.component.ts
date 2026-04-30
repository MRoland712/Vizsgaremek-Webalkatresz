import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FilterService } from '../../services/filter.service';

@Component({
  selector: 'app-filter-card',
  imports: [RouterLink],
  templateUrl: './filter-card.component.html',
  styleUrl: './filter-card.component.css',
})
export class CategoryCardComponent implements OnInit {
  private router = inject(Router);
  filterService = inject(FilterService);

  isLoading = computed(() => this.filterService.isLoading());
  error = signal<string | null>(null);

  // Kategóriák az aktív allParts-ból (jármű típus szűrőt is figyelembe veszi)
  categories = computed(() => {
    const parts = this.filterService.allParts();
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
          categoryMap.get(part.category)!.count++;
        } else {
          categoryMap.set(part.category, {
            name: part.category,
            count: 1,
            imageUrl: part.imageUrl || 'assets/placeholder.jpg',
            categoryUrl: this.normalizeCategory(part.category),
          });
        }
      }
    });
    return Array.from(categoryMap.values());
  });

  async ngOnInit(): Promise<void> {
    await this.filterService.loadData();
  }

  private normalizeCategory(category: string): string {
    return category
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/\s+/g, '-');
  }

  loadCategories() {
    this.filterService.loadData();
  }
}
