import { Component, inject, OnInit } from '@angular/core';
import { ProductCardComponent } from '../product-card/product-card.component';
import { Product } from './product.model';
import { GetallpartsService } from '../../../services/getallparts.service';
import { PartsModel } from '../../../models/parts.model';

/**
 * ==========================================
 * PRODUCT LIST KOMPONENS
 * ==========================================
 *
 * Ez a komponens megjeleníti a termékeket grid layoutban,
 * soronként 2 kártyával.
 */

@Component({
  selector: 'app-product-list',
  imports: [ProductCardComponent],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css',
})
export class ProductListComponent implements OnInit {
  productsService = inject(GetallpartsService);
  parts: PartsModel[] = [];

  ngOnInit(): void {
    this.loadPartCategories();
  }
  loadPartCategories() {
    this.productsService.getAllParts().subscribe({
      next: (response) => {
        this.parts = response.parts;
      },
    });
  }
}
