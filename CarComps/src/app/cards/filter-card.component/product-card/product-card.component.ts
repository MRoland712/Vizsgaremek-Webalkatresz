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
  private cartService = inject(CartService);

  product = input.required<PartsModel>();
  quantity = signal(1);

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
    this.quantity.update((current) => (current > 1 ? current - 1 : 1));
  }

  addToCart(): void {
    const product = this.product();
    const qty = this.quantity();

    this.cartService.addToCart({
      id: product.id,
      name: product.name,
      price: product.price,
      quantity: qty,
      imageUrl: product.imageUrl,
      sku: product.sku,
    });

    // Quantity visszaállítása 1-re (NINCS animáció)
    this.quantity.set(1);
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/placeholder.jpg';
  }
}
