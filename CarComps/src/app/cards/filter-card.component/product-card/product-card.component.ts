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

  addedToCart = signal(false); // vizuális visszajelzés

  productDetails = computed(() => [
    { label: 'Kategória', value: this.product().category },
    { label: 'Raktárkészlet', value: `${this.product().stock} db` },
    { label: 'Állapot', value: this.product().isActive ? 'Elérhető' : 'Nem elérhető' },
  ]);

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
    const product = this.product();
    const qty = this.quantity();

    // CartService kezeli a backend hívást is
    this.cartService.addToCart({
      id: product.id,
      name: product.name,
      price: product.price,
      quantity: qty,
      imageUrl: product.imageUrl,
      brand: String(product.manufacturerId ?? ''),
      sku: product.sku,
    });

    // Vizuális visszajelzés
    this.addedToCart.set(true);
    setTimeout(() => this.addedToCart.set(false), 2000);
    this.quantity.set(1);
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/placeholder.jpg';
  }
}
