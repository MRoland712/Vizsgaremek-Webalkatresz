import { Component, input, signal, computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { PartsModel } from '../../../models/parts.model';
import { CartService } from '../../../services/cart.service';

@Component({
  selector: 'app-product-card',
  imports: [],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.css',
})
export class ProductCardComponent {
  private router = inject(Router);
  private cartService = inject(CartService); // ⭐ CartService inject

  product = input.required<PartsModel>();
  quantity = signal(1); // ⭐ Alapértelmezett 1

  // Vizuális visszajelzés
  addedToCart = signal(false);

  productDetails = computed(() => [
    { label: 'Kategória', value: this.product().category },
    { label: 'Raktárkészlet', value: `${this.product().stock} db` },
    { label: 'Állapot', value: this.product().isActive ? 'Elérhető' : 'Nem elérhető' },
  ]);

  viewProductDetails(): void {
    this.router.navigate(['/product', this.product().id]);
  }

  increaseQuantity(): void {
    this.quantity.update((current) => current + 1);
  }

  decreaseQuantity(): void {
    // ⭐ Minimum 1
    this.quantity.update((current) => (current > 1 ? current - 1 : 1));
  }

  addToCart(): void {
    const product = this.product();
    const qty = this.quantity();

    // ⭐ CartService-en keresztül - Header automatikusan frissül
    this.cartService.addToCart({
      id: product.id,
      name: product.name,
      price: product.price,
      quantity: qty,
      imageUrl: product.imageUrl,
      sku: product.sku,
    });

    // ⭐ Vizuális visszajelzés - gomb zöldre vált 1.5mp-re
    this.addedToCart.set(true);
    setTimeout(() => this.addedToCart.set(false), 1500);

    // Quantity visszaállítása 1-re
    this.quantity.set(1);
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/placeholder.jpg';
  }
}
