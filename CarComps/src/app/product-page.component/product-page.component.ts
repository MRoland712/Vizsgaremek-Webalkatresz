import { Component, inject, OnInit, signal } from '@angular/core';
import { ProductListComponent } from '../cards/filter-card.component/product-list/product-list.component';
import { MainHeaderComponent } from '../main-header/main-header.component';
import { MmtContainerComponent } from '../mmt-container/mmt-container.component';
import { Filter } from '../filter/filter.component';
import { FooterComponent } from '../footer.component/footer.component';
import { ActivatedRoute } from '@angular/router';
import { GetallpartsService } from '../services/getallparts.service';
import { PartsModel } from '../models/parts.model';
import { DynamicBreadcrumbsComponent } from '../shared/dynamic-breadcrumbs.component/dynamic-breadcrumbs.component';

@Component({
  selector: 'app-product-page.component',
  imports: [
    ProductListComponent,
    MainHeaderComponent,
    MmtContainerComponent,
    Filter,
    FooterComponent,
    DynamicBreadcrumbsComponent,
  ],
  templateUrl: './product-page.component.html',
  styleUrl: './product-page.component.css',
})
export class ProductPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private partsService = inject(GetallpartsService);

  category = signal<string>('');
  allParts = signal<PartsModel[]>([]);
  filteredParts = signal<PartsModel[]>([]);
  isLoading = signal(false);

  ngOnInit(): void {
    // ⭐ PATH PARAM figyelése (nem queryParams!)
    this.route.params.subscribe((params) => {
      this.category.set(params['category'] || '');
      console.log('📦 Kategória (path param):', params['category']);
      this.loadAndFilter();
    });
  }

  loadAndFilter() {
    this.isLoading.set(true);

    this.partsService.getAllParts().subscribe({
      next: (response) => {
        if (response.success) {
          this.allParts.set(response.parts);

          const cat = this.category();

          if (cat) {
            const filtered = response.parts.filter(
              (p) => p.category.toLowerCase() === cat.toLowerCase(),
            );
            this.filteredParts.set(filtered);
            console.log(`✅ ${filtered.length} termék szűrve (${cat})`);
          } else {
            this.filteredParts.set(response.parts);
            console.log(`✅ ${response.parts.length} termék (összes)`);
          }
        }
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      },
    });
  }

  getCategoryName() {
    const cat = this.category();
    return cat || 'Összes termék';
  }
}
