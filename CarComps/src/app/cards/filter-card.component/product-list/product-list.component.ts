import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductCardComponent } from '../product-card/product-card.component';
import { BreadcrumbService } from '../../../services/breadcrumb.service';
import { FilterService } from '../../../services/filter.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [ProductCardComponent],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'],
})
export class ProductListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private breadcrumbService = inject(BreadcrumbService);
  filterService = inject(FilterService);

  currentCategory = signal<string>('');

  // ⭐ Ha nincs aktív filter → összes termék, nem URL alapján szűr
  filteredProducts = computed(() => {
    if (!this.filterService.hasActiveFilters()) {
      return this.filterService.allParts();
    }
    return this.filterService.filteredParts();
  });

  isLoading = computed(() => this.filterService.isLoading());

  async ngOnInit(): Promise<void> {
    await this.filterService.loadData();

    this.route.params.subscribe((params) => {
      const category = params['category'] || '';
      this.currentCategory.set(category);
      if (category) {
        this.breadcrumbService.setLastCategory(category);
      }
    });
  }
}
