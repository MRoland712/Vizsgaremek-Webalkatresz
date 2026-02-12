import { Component, input, signal, computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { PartsModel } from '../../../models/parts.model';

@Component({
  selector: 'app-product-card',
  imports: [],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.css',
})
export class ProductCardComponent {
  private router = inject(Router);

  // Termék adatok
  product = input.required<PartsModel>();

  // Quantity signal
  quantity = signal(0);

  // Computed property
  productDetails = computed(() => [
    { label: 'Kategória', value: this.product().category },
    { label: 'Raktárkészlet', value: `${this.product().stock} db` },
    { label: 'Állapot', value: this.product().isActive ? 'Elérhető' : 'Nem elérhető' },
  ]);

  /**
   * ⭐ Termék részletes oldalra navigálás
   */
  viewProductDetails(): void {
    const productId = this.product().id;
    console.log('🔍 Termék részletek megnyitása:', productId);

    // Navigáció termék részletes oldalra
    this.router.navigate(['/product', productId]);
  }

  increaseQuantity(): void {
    this.quantity.update((current) => current + 1);
  }

  decreaseQuantity(): void {
    this.quantity.update((current) => (current > 0 ? current - 1 : 0));
  }

  addToCart(): void {
    const currentQty = this.quantity();
    if (currentQty === 0) {
      return;
    }
    console.log('🛒 Kosárba:', {
      product: this.product().name,
      quantity: currentQty,
      totalPrice: this.product().price * currentQty,
    });
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/placeholder.jpg';
    console.warn('⚠️ Kép betöltési hiba:', this.product().name);
  }
}
