import { Component } from '@angular/core';
import { ProductCardComponent } from '../product-card/product-card.component';

/**
 * ==========================================
 * PRODUCT LIST KOMPONENS
 * ==========================================
 *
 * Ez a komponens megjeleníti a termékeket grid layoutban,
 * soronként 2 kártyával.
 */

// Termék interface (példa)
interface Product {
  id: number;
  name: string;
  articleNumber: string;
  price: number;
  image: string;
}

@Component({
  selector: 'app-product-list',
  imports: [ProductCardComponent],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css',
})
export class ProductListComponent {
  // ==========================================
  // PÉLDA TERMÉKEK
  // ==========================================
  products: Product[] = [
    {
      id: 1,
      name: 'Brake disc Front Axle STARK',
      articleNumber: 'SKBD-0020354',
      price: 11700,
      image: '/assets/brake-disc-1.png',
    },
    {
      id: 2,
      name: 'Brake Pad Set Front Axle',
      articleNumber: 'SKBP-0010245',
      price: 8990,
      image: '/assets/brake-pad-1.png',
    },
    {
      id: 3,
      name: 'Oil Filter Mann',
      articleNumber: 'W610/3',
      price: 2490,
      image: '/assets/oil-filter-1.png',
    },
    {
      id: 4,
      name: 'Air Filter Bosch',
      articleNumber: 'S0045',
      price: 3200,
      image: '/assets/air-filter-1.png',
    },
    {
      id: 5,
      name: 'Spark Plug NGK',
      articleNumber: 'PFR6Q',
      price: 1850,
      image: '/assets/spark-plug-1.png',
    },
    {
      id: 6,
      name: 'Wiper Blade Bosch',
      articleNumber: 'A402H',
      price: 4200,
      image: '/assets/wiper-blade-1.png',
    },
  ];

  // ==========================================
  // API-BÓL BETÖLTÉS (példa)
  // ==========================================

  // constructor(private productService: ProductService) {}

  // ngOnInit() {
  //   this.productService.getProducts().subscribe({
  //     next: (data) => {
  //       this.products = data;
  //     },
  //     error: (error) => {
  //       console.error('Hiba a termékek betöltésekor:', error);
  //     }
  //   });
  // }
}
