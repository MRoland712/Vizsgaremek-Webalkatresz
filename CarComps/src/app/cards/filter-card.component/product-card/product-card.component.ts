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
  isCooldown = signal(false);
  isOutOfStock = computed(() => (this.product().stock ?? 0) <= 0 || !this.product().isActive);

  private cooldownTimer: any;

  productDetails = computed(() => {
    const available = this.product().isActive && (this.product().stock ?? 0) > 0;
    return [
      { label: 'Kategória', value: this.product().category },
      { label: 'Raktárkészlet', value: `${this.product().stock} db` },
      { label: 'Állapot', value: available ? 'Elérhető' : 'Nem elérhető', available },
    ];
  });

  viewProductDetails(): void {
    this.router.navigate(['/product', this.product().id]);
  }

  increaseQuantity(): void {
    this.quantity.update((q) => q + 1);
  }

  decreaseQuantity(): void {
    this.quantity.update((q) => (q > 1 ? q - 1 : 1));
  }

  addToCart(): void {
    if (this.isCooldown() || this.isOutOfStock()) return;

    const product = this.product();
    this.cartService.addToCart({
      id: product.id,
      name: product.name,
      price: product.price,
      quantity: this.quantity(),
      imageUrl: product.imageUrl,
      brand: String(product.manufacturerId ?? ''),
      sku: product.sku,
    });

    this.quantity.set(1);
    this.isCooldown.set(true);

    clearTimeout(this.cooldownTimer);
    this.cooldownTimer = setTimeout(() => {
      this.isCooldown.set(false);
    }, 3000);
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/placeholder.jpg';
  }
}
