import { Component, input, OnInit, signal, computed } from '@angular/core';
import { PartsModel } from '../../../models/parts.model';

@Component({
  selector: 'app-product-card',
  imports: [],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.css',
})
export class ProductCardComponent {
  // Termék adatok
  product = input.required<PartsModel>();

  // Quantity signal - reactive state
  quantity = signal(0);

  // ✅ Computed property - automatikusan frissül!
  productDetails = computed(() => [
    { label: 'Kategória', value: this.product().category },
    { label: 'Raktárkészlet', value: `${this.product().stock} db` },
    { label: 'Állapot', value: this.product().isActive ? 'Elérhető' : 'Nem elérhető' },
  ]);

  /**
   * Mennyiség növelése
   */
  increaseQuantity(): void {
    this.quantity.update((current) => current + 1);
    console.log('📈 Quantity increased:', this.quantity());
  }

  /**
   * Mennyiség csökkentése (minimum 0)
   */
  decreaseQuantity(): void {
    this.quantity.update((current) => (current > 0 ? current - 1 : 0));
    console.log('📉 Quantity decreased:', this.quantity());
  }

  /**
   * Kosárba helyezés
   */
  addToCart(): void {
    const currentQty = this.quantity();
    if (currentQty === 0) {
      return;
    }

    // TODO: Cart service hívás
  }
}
