import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductListComponent } from '../cards/filter-card.component/product-list/product-list.component';
import { MainHeaderComponent } from '../main-header/main-header.component';
import { MmtContainerComponent } from '../mmt-container/mmt-container.component';
import { Filter } from '../filter/filter.component';
import { FooterComponent } from '../footer.component/footer.component';
import { DynamicBreadcrumbsComponent } from '../shared/dynamic-breadcrumbs.component/dynamic-breadcrumbs.component';
import { GetAllPartsWithImagesService } from '../services/getallpartswithimages.service';
import { PartWithImagesModel } from '../models/getallpartswithimages.model';

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
  private partsService = inject(GetAllPartsWithImagesService);

  category = signal<string>('');
  allParts = signal<PartWithImagesModel[]>([]);
  filteredParts = signal<PartWithImagesModel[]>([]);
  isLoading = signal(false);

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.category.set(params['category'] || '');
      this.loadAndFilter();
    });
  }

  loadAndFilter() {
    this.isLoading.set(true);
    this.partsService.getAllPartsWithImages().subscribe({
      next: (res) => {
        if (res.success) {
          this.allParts.set(res.parts);
          const cat = this.category();
          if (cat) {
            this.filteredParts.set(
              res.parts.filter((p) => p.category.toLowerCase() === cat.toLowerCase()),
            );
          } else {
            this.filteredParts.set(res.parts);
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
    return this.category() || 'Összes termék';
  }
}
