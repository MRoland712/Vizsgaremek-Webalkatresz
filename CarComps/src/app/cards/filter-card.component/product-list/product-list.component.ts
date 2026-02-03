import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductCardComponent } from '../product-card/product-card.component';
import { GetallpartsService } from '../../../services/getallparts.service';
import { PartsModel } from '../../../models/parts.model';

@Component({
  selector: 'app-product-list',
  imports: [ProductCardComponent],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css',
})
export class ProductListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private productsService = inject(GetallpartsService);

  parts: PartsModel[] = [];
  currentCategory: string = '';
  match = 0;
  CurrentCatname = '';

  ngOnInit(): void {
    // ⭐ PATH PARAM figyelése (nem queryParams!)
    this.route.params.subscribe((params) => {
      this.currentCategory = params['category'] || '';
      console.log('📦 Product List - Kategória szűrő:', this.currentCategory);

      // Termékek betöltése
      this.loadPartCategories();
    });
  }

  loadPartCategories() {
    this.productsService.getAllParts().subscribe({
      next: (response) => {
        this.CurrentCatname = this.currentCategory;
        // Szűrés kategóriára
        if (this.currentCategory) {
          // Van kategória → szűrés
          this.parts = response.parts.filter(
            (part) => part.category.toLowerCase() === this.currentCategory.toLowerCase(),
            (this.match = response.parts.filter(
              (part) => part.category.toLowerCase() === this.currentCategory.toLowerCase(),
            ).length),
          );
          console.log(`✅ Product List: ${this.parts.length} termék szűrve`);
        } else {
          // Nincs kategória → összes termék
          this.parts = response.parts;
          console.log(`✅ Product List: ${this.parts.length} termék (összes)`);
        }
      },
    });
  }
}
